package com.chsbk.mesh.client;

import com.chsbk.mesh.model.TransferRequest;
import com.chsbk.mesh.model.UserDto;
import com.chsbk.mesh.transport.MeshEndpoints;
import org.springframework.web.client.RestClient;

import java.util.Objects;

public class MeshRestClient {

    private final RestClient restClient;

    public MeshRestClient(RestClient restClient) {
        this.restClient = Objects.requireNonNull(restClient);
    }

    public UserDto getUser(String accessToken, long userId) {
        return restClient.get()
                .uri(MeshEndpoints.USERS_ROOT + MeshEndpoints.USER_BY_ID, userId)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .body(UserDto.class);
    }

    public UserDto getCurrentUser(String accessToken) {
        return restClient.get()
                .uri(MeshEndpoints.USERS_ROOT + MeshEndpoints.CURRENT_USER)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .body(UserDto.class);
    }

    public void transfer(String accessToken, TransferRequest request) {
        restClient.post()
                .uri(MeshEndpoints.TRANSFERS_ROOT)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }
}
