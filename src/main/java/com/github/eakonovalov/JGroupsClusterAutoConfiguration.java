/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.eakonovalov;

import org.jgroups.JChannel;
import org.jgroups.Receiver;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Evgeny Konovalov
 */
@Configuration
@ConditionalOnClass(name = "org.jgroups.JChannel")
@ConditionalOnProperty(value = "jgroups.cluster.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(JGroupsClusterProperties.class)
public class JGroupsClusterAutoConfiguration {

    private final JGroupsClusterProperties cluster;
    private final Receiver receiver;

    public JGroupsClusterAutoConfiguration(JGroupsClusterProperties cluster, ObjectProvider<Receiver> receiverProvider) {
        this.cluster = cluster;
        this.receiver = receiverProvider.getIfAvailable();
    }

    @Bean(destroyMethod = "close")
    public JChannel channel() throws Exception {
        final JChannel channel = new JChannel();
        if (receiver != null) {
            channel.setReceiver(receiver);
        }
        channel.connect(cluster.getName() != null ? cluster.getName() : "default");

        return channel;
    }
}
