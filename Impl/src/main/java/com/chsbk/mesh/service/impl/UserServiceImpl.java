package com.chsbk.mesh.service.impl;

import com.chsbk.mesh.configuration.CacheNames;
import com.chsbk.mesh.model.UserDto;
import com.chsbk.mesh.model.entity.EmailData;
import com.chsbk.mesh.model.entity.PhoneData;
import com.chsbk.mesh.model.entity.User;
import com.chsbk.mesh.repository.UserRepository;
import com.chsbk.mesh.service.UserMapper;
import com.chsbk.mesh.service.UserService;
import com.chsbk.mesh.service.event.UsersChangedEvent;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = CacheNames.USER_BY_ID, key = "#userId")
    public UserDto getUser(long userId) {
        return userMapper.toDto(getUserOrThrow(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> findUsers(String name, String email, String phone, LocalDate dateOfBirth, Pageable pageable) {
        return userRepository.search(name, email, phone, dateOfBirth, pageable).map(userMapper::toDto);
    }

    @Override
    @Transactional
    public void replaceEmails(long userId, Set<String> emails) {
        User user = getUserOrThrow(userId);
        for (String email : emails) {
            ensureEmailIsAvailable(userId, email);
        }
        user.getEmails().removeIf(existing -> !emails.contains(existing.getEmail()));
        for (String email : emails) {
            if (user.getEmails().stream().noneMatch(existing -> existing.getEmail().equals(email))) {
                user.addEmail(EmailData.builder().email(email).build());
            }
        }
        signalUserChanged(userId);
    }

    @Override
    @Transactional
    public void addEmail(long userId, String email) {
        ensureEmailIsAvailable(userId, email);
        User user = getUserOrThrow(userId);
        if (user.getEmails().stream().anyMatch(existing -> existing.getEmail().equals(email))) {
            return;
        }
        user.addEmail(EmailData.builder().email(email).build());
        signalUserChanged(userId);
    }

    @Override
    @Transactional
    public void removeEmail(long userId, String email) {
        User user = getUserOrThrow(userId);
        if (!user.getEmails().removeIf(existing -> existing.getEmail().equals(email))) {
            throw new EntityNotFoundException("Email не найден");
        }
        signalUserChanged(userId);
    }

    @Override
    @Transactional
    public void updateEmail(long userId, String previousEmail, String newEmail) {
        if (previousEmail.equals(newEmail)) {
            User user = getUserOrThrow(userId);
            if (user.getEmails().stream().noneMatch(existing -> existing.getEmail().equals(previousEmail))) {
                throw new EntityNotFoundException("Email не найден");
            }
            return;
        }
        ensureEmailIsAvailable(userId, newEmail);
        User user = getUserOrThrow(userId);
        if (!user.getEmails().removeIf(existing -> existing.getEmail().equals(previousEmail))) {
            throw new EntityNotFoundException("Email не найден");
        }
        user.addEmail(EmailData.builder().email(newEmail).build());
        signalUserChanged(userId);
    }

    @Override
    @Transactional
    public void replacePhones(long userId, Set<String> phones) {
        User user = getUserOrThrow(userId);
        for (String phone : phones) {
            ensurePhoneIsAvailable(userId, phone);
        }
        user.getPhones().removeIf(existing -> !phones.contains(existing.getPhone()));
        for (String phone : phones) {
            if (user.getPhones().stream().noneMatch(existing -> existing.getPhone().equals(phone))) {
                user.addPhone(PhoneData.builder().phone(phone).build());
            }
        }
        signalUserChanged(userId);
    }

    @Override
    @Transactional
    public void addPhone(long userId, String phone) {
        ensurePhoneIsAvailable(userId, phone);
        User user = getUserOrThrow(userId);
        if (user.getPhones().stream().anyMatch(existing -> existing.getPhone().equals(phone))) {
            return;
        }
        user.addPhone(PhoneData.builder().phone(phone).build());
        signalUserChanged(userId);
    }

    @Override
    @Transactional
    public void removePhone(long userId, String phone) {
        User user = getUserOrThrow(userId);
        if (!user.getPhones().removeIf(existing -> existing.getPhone().equals(phone))) {
            throw new EntityNotFoundException("Телефон не найден");
        }
        signalUserChanged(userId);
    }

    @Override
    @Transactional
    public void updatePhone(long userId, String previousPhone, String newPhone) {
        if (previousPhone.equals(newPhone)) {
            User user = getUserOrThrow(userId);
            if (user.getPhones().stream().noneMatch(existing -> existing.getPhone().equals(previousPhone))) {
                throw new EntityNotFoundException("Телефон не найден");
            }
            return;
        }
        ensurePhoneIsAvailable(userId, newPhone);
        User user = getUserOrThrow(userId);
        if (!user.getPhones().removeIf(existing -> existing.getPhone().equals(previousPhone))) {
            throw new EntityNotFoundException("Телефон не найден");
        }
        user.addPhone(PhoneData.builder().phone(newPhone).build());
        signalUserChanged(userId);
    }

    private User getUserOrThrow(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));
    }

    private void ensureEmailIsAvailable(long userId, String email) {
        userRepository.findByEmails_Email(email)
                .filter(existingUser -> existingUser.getId() != userId)
                .ifPresent(existingUser -> {
                    throw new IllegalArgumentException("Email уже используется: " + email);
                });
    }

    private void ensurePhoneIsAvailable(long userId, String phone) {
        userRepository.findByPhones_Phone(phone)
                .filter(existingUser -> existingUser.getId() != userId)
                .ifPresent(existingUser -> {
                    throw new IllegalArgumentException("Телефон уже используется: " + phone);
                });
    }

    private void signalUserChanged(long userId) {
        eventPublisher.publishEvent(new UsersChangedEvent(Set.of(userId)));
    }
}
