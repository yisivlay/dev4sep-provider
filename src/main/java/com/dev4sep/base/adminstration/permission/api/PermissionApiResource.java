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
package com.dev4sep.base.adminstration.permission.api;

import com.dev4sep.base.adminstration.permission.data.PermissionData;
import com.dev4sep.base.adminstration.permission.handler.PermissionCommandWrapperBuilder;
import com.dev4sep.base.adminstration.permission.service.PermissionReadPlatformService;
import com.dev4sep.base.config.api.ApiRequestParameterHelper;
import com.dev4sep.base.config.command.domain.CommandWrapper;
import com.dev4sep.base.config.command.service.CommandSourceWritePlatformService;
import com.dev4sep.base.config.data.RequestParameters;
import com.dev4sep.base.config.security.service.PlatformSecurityContext;
import com.dev4sep.base.config.serialization.DefaultToApiJsonSerializer;
import com.dev4sep.base.config.service.Page;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author YISivlay
 */
@Component
@Path("/v1" + PermissionApiConstants.PATH)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PermissionApiResource {

    private final PlatformSecurityContext context;
    private final DefaultToApiJsonSerializer<PermissionData> toApiJsonSerializer;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final CommandSourceWritePlatformService commandSourceWritePlatformService;
    private final PermissionReadPlatformService permissionReadPlatformService;

    @GET
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String getAllPermissions(@Context final UriInfo uriInfo,
                                    @QueryParam("orderBy") final String orderBy, @QueryParam("sortOrder") final String sortOrder,
                                    @QueryParam("offset") final Integer offset, @QueryParam("limit") final Integer limit) {
        this.context.authenticatedUser().validateHasReadPermission(PermissionApiConstants.PERMISSIONS);
        var requestParameters = RequestParameters.builder().orderBy(orderBy).sortOrder(sortOrder).offset(offset).limit(limit).build();

        final var settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());

        Page<PermissionData> page;
        if (settings.isMakerChecker()) {
            page = this.permissionReadPlatformService.getAllMakerCheckerPermissions(requestParameters);
        } else {
            page = this.permissionReadPlatformService.getAllPermissions(requestParameters);
        }
        if (settings.isPagination()) {
            return this.toApiJsonSerializer.serialize(settings, page, PermissionApiConstants.RESPONSE_PARAMETERS);
        }
        return this.toApiJsonSerializer.serialize(settings, page.getPageItems(), PermissionApiConstants.RESPONSE_PARAMETERS);
    }

    @PUT
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String updatePermissionDetails(final String jsonBody) {
        this.context.authenticatedUser().validateHasUpdatePermission(PermissionApiConstants.PERMISSIONS);
        final CommandWrapper request = new PermissionCommandWrapperBuilder().update().json(jsonBody).build();
        final var result = this.commandSourceWritePlatformService.logCommandSource(request);
        return this.toApiJsonSerializer.serialize(result);
    }

}
