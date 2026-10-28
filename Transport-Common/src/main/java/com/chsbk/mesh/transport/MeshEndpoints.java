package com.chsbk.mesh.transport;

public final class MeshEndpoints {

    public static final String AUTH_ROOT = "/auth";
    public static final String LOGIN = "/login";
    public static final String LOGOUT = "/logout";
    public static final String USERS_ROOT = "/users";
    public static final String TRANSFERS_ROOT = "/transfers";
    public static final String USER_BY_ID = "/{id}";
    public static final String CURRENT_USER = "/me";
    public static final String AUTH_COOKIE_NAME = "AuthToken";
    public static final String CORRELATION_HEADER_NAME = "X-Correlation-Id";

    private MeshEndpoints() {
        throw new UnsupportedOperationException("Utility class");
    }
}
