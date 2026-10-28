package com.chsbk.mesh.service;

import com.chsbk.mesh.model.UserDto;
import com.chsbk.mesh.model.entity.EmailData;
import com.chsbk.mesh.model.entity.PhoneData;
import com.chsbk.mesh.model.entity.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getDateOfBirth(),
                user.getEmails().stream().map(EmailData::getEmail).collect(Collectors.toSet()),
                user.getPhones().stream().map(PhoneData::getPhone).collect(Collectors.toSet()),
                user.getAccount().getBalance(),
                user.getAccount().getInitialBalance()
        );
    }
}
