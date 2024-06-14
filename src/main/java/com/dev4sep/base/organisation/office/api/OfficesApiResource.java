/**
 *    Copyright 2024 DEV4Sep
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.dev4sep.base.organisation.office.api;

import com.dev4sep.base.config.api.ApiRequestParameterHelper;
import com.dev4sep.base.config.data.RequestParameters;
import com.dev4sep.base.config.security.service.PlatformSecurityContext;
import com.dev4sep.base.config.serialization.ApiRequestJsonSerializationSettings;
import com.dev4sep.base.config.serialization.DefaultToApiJsonSerializer;
import com.dev4sep.base.config.service.Page;
import com.dev4sep.base.organisation.office.data.OfficeData;
import com.dev4sep.base.organisation.office.service.OfficeReadPlatformService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author YISivlay
 */
@Path("/v1/offices")
@Component
@RequiredArgsConstructor
public class OfficesApiResource {

    private final PlatformSecurityContext context;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final DefaultToApiJsonSerializer<OfficeData> toApiJsonSerializer;
    private final OfficeReadPlatformService officeReadPlatformService;

    @GET
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String getAllOffice(@Context final UriInfo uriInfo,
                               @DefaultValue("false") @QueryParam("includeAllOffices") final boolean includeAllOffices,
                               @QueryParam("orderBy") final String orderBy, @QueryParam("sortOrder") final String sortOrder,
                               @QueryParam("offset") final Integer offset, @QueryParam("limit") final Integer limit) {
        this.context.authenticatedUser().validateHasReadPermission(OfficesApiConstants.PERMISSIONS);

        RequestParameters requestParameters = RequestParameters.builder().orderBy(orderBy).sortOrder(sortOrder).offset(offset).limit(limit).build();
        Page<OfficeData> offices = this.officeReadPlatformService.getAllOffices(includeAllOffices, requestParameters);

        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        if (settings.isPagination()) {
            return this.toApiJsonSerializer.serialize(settings, offices, OfficesApiConstants.RESPONSE_PARAMETERS);
        }
        return this.toApiJsonSerializer.serialize(settings, offices.getPageItems(), OfficesApiConstants.RESPONSE_PARAMETERS);
    }

    @GET
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String getOneOffice(@Context final UriInfo uriInfo,
                               @PathParam("id") final Long id) {

        this.context.authenticatedUser().validateHasReadPermission(OfficesApiConstants.PERMISSIONS);

        OfficeData office = this.officeReadPlatformService.getOneOffices(id);

        final ApiRequestJsonSerializationSettings settings = apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, office, OfficesApiConstants.RESPONSE_PARAMETERS);
    }
}
