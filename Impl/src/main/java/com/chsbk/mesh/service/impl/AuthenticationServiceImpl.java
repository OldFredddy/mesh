package com.chsbk.mesh.service.impl;

import com.chsbk.mesh.model.entity.User;
import com.chsbk.mesh.repository.UserRepository;
import com.chsbk.mesh.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public long authenticate(String login, String password) {
        User user = userRepository.findByEmails_Email(login)
                .or(() -> userRepository.findByPhones_Phone(login))
                .orElseThrow(() -> new BadCredentialsException("Неверный логин или пароль"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Неверный логин или пароль");
        }
        return user.getId();
    }
}
