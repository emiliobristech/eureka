/*
 * Copyright 2014 Netflix, Inc.
 *
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

package com.netflix.eureka2.server.spi;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Properties;
import java.util.ServiceLoader;

import com.netflix.eureka2.registry.SourcedEurekaRegistry;
import com.netflix.eureka2.registry.instance.InstanceInfo;
import com.netflix.eureka2.server.config.EurekaServerConfig;

/**
 * Eureka extensions discovery is based on {@link ServiceLoader} mechanism.
 * For seamless configuration this class provided basic information, that should
 * be sufficient for the extension bootstrapping.
 *
 * @author Tomasz Bak
 */
@Singleton
public class ExtensionContext {

    public static final String PROPERTY_KEYS_PREFIX = "eureka.ext";

    private final EurekaServerConfig config;
    private final SourcedEurekaRegistry<InstanceInfo> localRegistry;

    @Inject
    protected ExtensionContext(EurekaServerConfig config, SourcedEurekaRegistry localRegistry) {
        this.config = config;
        this.localRegistry = localRegistry;
    }

    public EurekaServerConfig getConfig() {
        return config;
    }

    /**
     * Unique name assigned to read or write cluster.
     */
    public String getEurekaClusterName() {
        return config.getAppName();
    }

    public SourcedEurekaRegistry<InstanceInfo> getLocalRegistry() {
        return localRegistry;
    }

    public static class ExtensionContextBuilder {

        private String eurekaClusterName;
        private Properties properties;
        private boolean addSystemProperties;

        public ExtensionContextBuilder withEurekaClusterName(String eurekaClusterName) {
            this.eurekaClusterName = eurekaClusterName;
            return this;
        }

        public ExtensionContextBuilder withProperties(Properties properties) {
            this.properties = properties;
            return this;
        }

        public ExtensionContextBuilder withSystemProperties(boolean addSystemProperties) {
            this.addSystemProperties = addSystemProperties;
            return this;
        }

        public ExtensionContext build() {
            Properties allProperties = new Properties(properties);
            if (addSystemProperties) {
                for (Object keyObj : System.getProperties().keySet()) {
                    if (keyObj instanceof String) {
                        String key = (String) keyObj;
                        if (key.startsWith(PROPERTY_KEYS_PREFIX)) {
                            allProperties.setProperty(key, System.getProperty(key));
                        }
                    }
                }
            }
            return null;
        }
    }
}