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
package com.dev4sep.base.adminstration.user.api;

import com.dev4sep.base.adminstration.user.data.UserData;
import com.dev4sep.base.adminstration.user.service.UserReadPlatformService;
import com.dev4sep.base.config.api.ApiRequestParameterHelper;
import com.dev4sep.base.config.data.RequestParameters;
import com.dev4sep.base.config.security.service.PlatformSecurityContext;
import com.dev4sep.base.config.serialization.ApiRequestJsonSerializationSettings;
import com.dev4sep.base.config.serialization.DefaultToApiJsonSerializer;
import com.dev4sep.base.config.service.Page;
import com.dev4sep.base.organisation.office.api.OfficesApiConstants;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author YISivlay
 */
@Component
@RequiredArgsConstructor
@Path("/v1" + UserApiConstants.PATH)
public class UserApiResource {

    private final PlatformSecurityContext context;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final DefaultToApiJsonSerializer<UserData> toApiJsonSerializer;
    private final UserReadPlatformService userReadPlatformService;


    @GET
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String getAllUsers(@Context final UriInfo uriInfo,
                              @QueryParam("orderBy") final String orderBy, @QueryParam("sortOrder") final String sortOrder,
                              @QueryParam("offset") final Integer offset, @QueryParam("limit") final Integer limit) {

        this.context.authenticatedUser().validateHasReadPermission(UserApiConstants.PERMISSIONS);
        var requestParameters = RequestParameters.builder().orderBy(orderBy).sortOrder(sortOrder).offset(offset).limit(limit).build();

        final var users = this.userReadPlatformService.getAllUsers(requestParameters);

        final var settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        if (settings.isPagination()) {
            return this.toApiJsonSerializer.serialize(settings, users, UserApiConstants.RESPONSE_PARAMETERS);
        }
        return this.toApiJsonSerializer.serialize(settings, users.getPageItems(), UserApiConstants.RESPONSE_PARAMETERS);
    }
}
