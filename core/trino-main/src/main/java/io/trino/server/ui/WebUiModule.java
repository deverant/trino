/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.trino.server.ui;

import com.google.inject.Binder;
import com.google.inject.Scopes;
import io.airlift.configuration.AbstractConfigurationAwareModule;

import static io.airlift.configuration.ConfigBinder.configBinder;
import static io.airlift.jaxrs.JaxrsBinder.jaxrsBinder;

public class WebUiModule
        extends AbstractConfigurationAwareModule
{
    @Override
    protected void setup(Binder binder)
    {
        jaxrsBinder(binder).bind(WebUiStaticResource.class);

        configBinder(binder).bindConfig(WebUiConfig.class);

        if (buildConfigObject(WebUiConfig.class).isEnabled()) {
            install(new WebUiAuthenticationModule());
            jaxrsBinder(binder).bind(ClusterResource.class);
            jaxrsBinder(binder).bind(ClusterStatsResource.class);
            jaxrsBinder(binder).bind(UiQueryResource.class);

            if (buildConfigObject(WebUiConfig.class).isPreviewEnabled()) {
                install(new WebUiPreviewModule());
            }
        }
        else {
            binder.bind(WebUiAuthenticationFilter.class).to(DisabledWebUiAuthenticationFilter.class).in(Scopes.SINGLETON);
        }
    }
}
