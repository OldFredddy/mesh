package com.chsbk.mesh.client;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

@ConfigurationProperties(prefix = "mesh.client")
public class MeshClientProperties {

    private URI baseUrl = URI.create("http://localhost:8080");

    public URI getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(URI baseUrl) {
        this.baseUrl = baseUrl;
    }
}
