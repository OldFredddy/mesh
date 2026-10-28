package com.chsbk.mesh.controller;

import com.chsbk.mesh.model.LoginRequest;
import com.chsbk.mesh.security.JwtService;
import com.chsbk.mesh.service.AuthenticationService;
import com.chsbk.mesh.transport.MeshEndpoints;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(MeshEndpoints.AUTH_ROOT)
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    @PostMapping(MeshEndpoints.LOGIN)
    public ResponseEntity<Void> login(@Valid @RequestBody LoginRequest request,
                                      HttpServletRequest servletRequest) {
        long userId = authenticationService.authenticate(request.login(), request.password());
        String token = jwtService.generateToken(userId);
        ResponseCookie authenticationCookie = ResponseCookie.from(MeshEndpoints.AUTH_COOKIE_NAME, token)
                .httpOnly(true)
                .secure(servletRequest.isSecure())
                .sameSite("Strict")
                .path("/")
                .build();

        log.info("User authenticated: userId={}", userId);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authenticationCookie.toString())
                .build();
    }

    @GetMapping(MeshEndpoints.LOGOUT)
    public ResponseEntity<Void> logout(HttpServletRequest servletRequest) {
        ResponseCookie expiredCookie = ResponseCookie.from(MeshEndpoints.AUTH_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(servletRequest.isSecure())
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.SET_COOKIE, expiredCookie.toString())
                .header(HttpHeaders.LOCATION, "/")
                .build();
    }
}
