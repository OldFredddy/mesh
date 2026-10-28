package com.chsbk.mesh.service;

public interface AuthenticationService {

    long authenticate(String login, String password);
}
