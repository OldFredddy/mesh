package com.chsbk.mesh.client;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(MeshClientProperties.class)
public class MeshClientConfiguration {

    @Bean
    MeshRestClient meshRestClient(MeshClientProperties properties, RestClient.Builder restClientBuilder) {
        RestClient restClient = restClientBuilder.baseUrl(properties.getBaseUrl().toString()).build();
        return new MeshRestClient(restClient);
    }
}
