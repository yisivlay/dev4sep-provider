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
package com.dev4sep.base.config.cache.api;

import com.dev4sep.base.config.api.ApiRequestParameterHelper;
import com.dev4sep.base.config.cache.data.CacheData;
import com.dev4sep.base.config.cache.handler.CacheCommandWrapperBuilder;
import com.dev4sep.base.config.cache.service.RuntimeDelegatingCacheManager;
import com.dev4sep.base.config.command.domain.CommandProcessing;
import com.dev4sep.base.config.command.domain.CommandWrapper;
import com.dev4sep.base.config.command.service.CommandSourceWritePlatformService;
import com.dev4sep.base.config.security.service.PlatformSecurityContext;
import com.dev4sep.base.config.serialization.ApiRequestJsonSerializationSettings;
import com.dev4sep.base.config.serialization.DefaultToApiJsonSerializer;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author YISivlay
 */
@Component
@Path("/v1" + CacheApiConstants.PATH)
public class CacheApiResource {

    public final PlatformSecurityContext context;
    private final RuntimeDelegatingCacheManager cacheService;
    private final ApiRequestParameterHelper apiRequestParameterHelper;
    private final CommandSourceWritePlatformService commandSourceWritePlatformService;
    private final DefaultToApiJsonSerializer<CacheData> toApiJsonSerializer;

    public CacheApiResource(final PlatformSecurityContext context,
                            @Qualifier("runtimeDelegatingCacheManager") final RuntimeDelegatingCacheManager cacheService,
                            final ApiRequestParameterHelper apiRequestParameterHelper,
                            final CommandSourceWritePlatformService commandSourceWritePlatformService,
                            final DefaultToApiJsonSerializer<CacheData> toApiJsonSerializer) {
        this.context = context;
        this.cacheService = cacheService;
        this.apiRequestParameterHelper = apiRequestParameterHelper;
        this.commandSourceWritePlatformService = commandSourceWritePlatformService;
        this.toApiJsonSerializer = toApiJsonSerializer;
    }

    @GET
    public String getAll(@Context UriInfo uriInfo) {
        this.context.authenticatedUser().validateHasReadPermission(CacheApiConstants.PERMISSIONS);

        final List<CacheData> caches = this.cacheService.getAll();
        final ApiRequestJsonSerializationSettings settings = this.apiRequestParameterHelper.process(uriInfo.getQueryParameters());
        return this.toApiJsonSerializer.serialize(settings, caches, CacheApiConstants.RESPONSE_PARAMETERS);
    }

    @PUT
    public String switchCache(final String jsonBody) {
        final CommandWrapper request = new CacheCommandWrapperBuilder().update().json(jsonBody).build();
        final CommandProcessing result = this.commandSourceWritePlatformService.logCommandSource(request);
        return this.toApiJsonSerializer.serialize(result);
    }
}
