package com.chsbk.mesh.controller;

import com.chsbk.mesh.model.UserDto;
import com.chsbk.mesh.service.UserService;
import com.chsbk.mesh.transport.MeshEndpoints;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Set;

@RestController
@RequestMapping(MeshEndpoints.USERS_ROOT)
@Validated
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public Page<UserDto> findUsers(@RequestParam(required = false) String name,
                                   @RequestParam(required = false) String email,
                                   @RequestParam(required = false) String phone,
                                   @RequestParam(required = false) LocalDate dateOfBirth,
                                   @RequestParam(defaultValue = "0") @Min(0) int page,
                                   @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return userService.findUsers(name, email, phone, dateOfBirth, PageRequest.of(page, size));
    }

    @GetMapping(MeshEndpoints.CURRENT_USER)
    public UserDto currentUser(@AuthenticationPrincipal UserDetails principal) {
        return userService.getUser(Long.parseLong(principal.getUsername()));
    }

    @GetMapping(MeshEndpoints.USER_BY_ID)
    public UserDto getUser(@PathVariable @Positive long id) {
        return userService.getUser(id);
    }

    @PostMapping("/{id}/update-emails")
    public void replaceEmails(@PathVariable @Positive long id,
                              @AuthenticationPrincipal UserDetails principal,
                              @RequestBody @NotEmpty Set<@Email String> emails) {
        requireOwner(id, principal);
        userService.replaceEmails(id, emails);
    }

    @PostMapping("/{id}/update-phones")
    public void replacePhones(@PathVariable @Positive long id,
                              @AuthenticationPrincipal UserDetails principal,
                              @RequestBody @NotEmpty Set<@Pattern(regexp = "\\d{11,13}") String> phones) {
        requireOwner(id, principal);
        userService.replacePhones(id, phones);
    }

    @PostMapping("/{id}/emails")
    public void addEmail(@PathVariable @Positive long id,
                         @AuthenticationPrincipal UserDetails principal,
                         @RequestBody @NotBlank @Email String email) {
        requireOwner(id, principal);
        userService.addEmail(id, email);
    }

    @DeleteMapping("/{id}/emails/{email}")
    public void removeEmail(@PathVariable @Positive long id,
                            @AuthenticationPrincipal UserDetails principal,
                            @PathVariable @Email String email) {
        requireOwner(id, principal);
        userService.removeEmail(id, email);
    }

    @PostMapping("/{id}/emails/{previousEmail}")
    public void updateEmail(@PathVariable @Positive long id,
                            @AuthenticationPrincipal UserDetails principal,
                            @PathVariable @Email String previousEmail,
                            @RequestBody @NotBlank @Email String newEmail) {
        requireOwner(id, principal);
        userService.updateEmail(id, previousEmail, newEmail);
    }

    @PostMapping("/{id}/phones")
    public void addPhone(@PathVariable @Positive long id,
                         @AuthenticationPrincipal UserDetails principal,
                         @RequestBody @Pattern(regexp = "\\d{11,13}") String phone) {
        requireOwner(id, principal);
        userService.addPhone(id, phone);
    }

    @DeleteMapping("/{id}/phones/{phone}")
    public void removePhone(@PathVariable @Positive long id,
                            @AuthenticationPrincipal UserDetails principal,
                            @PathVariable @Pattern(regexp = "\\d{11,13}") String phone) {
        requireOwner(id, principal);
        userService.removePhone(id, phone);
    }

    @PostMapping("/{id}/phones/{previousPhone}")
    public void updatePhone(@PathVariable @Positive long id,
                            @AuthenticationPrincipal UserDetails principal,
                            @PathVariable @Pattern(regexp = "\\d{11,13}") String previousPhone,
                            @RequestBody @Pattern(regexp = "\\d{11,13}") String newPhone) {
        requireOwner(id, principal);
        userService.updatePhone(id, previousPhone, newPhone);
    }

    private void requireOwner(long userId, UserDetails principal) {
        if (userId != Long.parseLong(principal.getUsername())) {
            throw new AccessDeniedException("Изменение чужого профиля запрещено");
        }
    }
}
