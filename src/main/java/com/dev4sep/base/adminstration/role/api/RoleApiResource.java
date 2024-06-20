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
package com.dev4sep.base.adminstration.role.api;

import com.dev4sep.base.adminstration.role.data.RoleData;
import com.dev4sep.base.adminstration.role.handler.RoleCommandWrapperBuilder;
import com.dev4sep.base.adminstration.role.service.RoleReadPlatformService;
import com.dev4sep.base.config.api.ApiRequestParameterHelper;
import com.dev4sep.base.config.command.domain.CommandWrapper;
import com.dev4sep.base.config.command.service.CommandSourceWritePlatformService;
import com.dev4sep.base.config.data.RequestParameters;
import com.dev4sep.base.config.security.service.PlatformSecurityContext;
import com.dev4sep.base.config.serialization.DefaultToApiJsonSerializer;
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
@Path("/v1" + RoleApiConstants.PATH)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RoleApiResource {

    private final PlatformSecurityContext context;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final DefaultToApiJsonSerializer<RoleData> toApiJsonSerializer;
    private final CommandSourceWritePlatformService commandSourceWritePlatformService;
    private final RoleReadPlatformService roleReadPlatformService;

    @GET
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String getAllRoles(@Context final UriInfo uriInfo,
                              @QueryParam("orderBy") final String orderBy, @QueryParam("sortOrder") final String sortOrder,
                              @QueryParam("offset") final Integer offset, @QueryParam("limit") final Integer limit) {

        this.context.authenticatedUser().validateHasReadPermission(RoleApiConstants.PERMISSIONS);
        var requestParameters = RequestParameters.builder().orderBy(orderBy).sortOrder(sortOrder).offset(offset).limit(limit).build();

        final var users = this.roleReadPlatformService.getAllRoles(requestParameters);

        final var settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        if (settings.isPagination()) {
            return this.toApiJsonSerializer.serialize(settings, users, RoleApiConstants.RESPONSE_PARAMETERS);
        }
        return this.toApiJsonSerializer.serialize(settings, users.getPageItems(), RoleApiConstants.RESPONSE_PARAMETERS);
    }

    @GET
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String getOneRole(@PathParam("id") final Long id, @Context final UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(RoleApiConstants.PERMISSIONS);
        final var user = this.roleReadPlatformService.getOneRole(id);
        final var settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, user, RoleApiConstants.RESPONSE_PARAMETERS);
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String createUser(final String jsonBody) {
        this.context.authenticatedUser().validateHasCreatePermission(RoleApiConstants.PERMISSIONS);
        final CommandWrapper request = new RoleCommandWrapperBuilder().create().json(jsonBody).build();
        final var result = this.commandSourceWritePlatformService.logCommandSource(request);
        return this.toApiJsonSerializer.serialize(result);
    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String updateUser(@PathParam("id") final Long id, final String jsonBody) {
        this.context.authenticatedUser().validateHasUpdatePermission(RoleApiConstants.PERMISSIONS);
        final CommandWrapper request = new RoleCommandWrapperBuilder().update(id).json(jsonBody).build();
        final var result = this.commandSourceWritePlatformService.logCommandSource(request);
        return this.toApiJsonSerializer.serialize(result);
    }

    @DELETE
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String deleteUser(@PathParam("id") final Long id) {
        this.context.authenticatedUser().validateHasDeletePermission(RoleApiConstants.PERMISSIONS);
        final CommandWrapper request = new RoleCommandWrapperBuilder().delete(id).build();
        final var result = this.commandSourceWritePlatformService.logCommandSource(request);
        return this.toApiJsonSerializer.serialize(result);
    }
}
