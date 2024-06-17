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
package com.dev4sep.base.config;

import com.dev4sep.base.config.datasource.database.domain.PlatformTenant;
import com.dev4sep.base.config.domain.ActionContext;
import com.dev4sep.base.config.domain.Context;

/**
 * @author YISivlay
 */
public final class ThreadLocalContextUtil {

    public static final String CONTEXT_TENANTS = "tenants";
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();
    private static final ThreadLocal<PlatformTenant> tenantContext = new ThreadLocal<>();
    private static final ThreadLocal<String> authTokenContext = new ThreadLocal<>();
    private static final ThreadLocal<ActionContext> actionContext = new ThreadLocal<>();

    public static String getDataSourceContext() {
        return contextHolder.get();
    }

    public static void reset() {
        contextHolder.remove();
        tenantContext.remove();
    }

    public static PlatformTenant getTenant() {
        return tenantContext.get();
    }

    public static void setTenant(final PlatformTenant tenant) {
        tenantContext.set(tenant);
    }

    public static void setAuthToken(final String authToken) {
        authTokenContext.set(authToken);
    }

    public static String getAuthToken() {
        return authTokenContext.get();
    }

    public static ActionContext getActionContext() {
        return actionContext.get() == null ? ActionContext.DEFAULT : actionContext.get();
    }

    public static Context getContext() {
        return new Context(getDataSourceContext(), getTenant(), getAuthToken(), getActionContext());
    }
}
