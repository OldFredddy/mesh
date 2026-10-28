package com.chsbk.mesh.service;

import com.chsbk.mesh.model.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Set;

public interface UserService {

    UserDto getUser(long userId);

    Page<UserDto> findUsers(String name, String email, String phone, LocalDate dateOfBirth, Pageable pageable);

    void replaceEmails(long userId, Set<String> emails);

    void addEmail(long userId, String email);

    void removeEmail(long userId, String email);

    void updateEmail(long userId, String previousEmail, String newEmail);

    void replacePhones(long userId, Set<String> phones);

    void addPhone(long userId, String phone);

    void removePhone(long userId, String phone);

    void updatePhone(long userId, String previousPhone, String newPhone);
}
