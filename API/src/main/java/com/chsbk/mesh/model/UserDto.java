package com.chsbk.mesh.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public record UserDto(
        Long id,
        String name,
        LocalDate dateOfBirth,
        Set<String> emails,
        Set<String> phones,
        BigDecimal balance,
        BigDecimal initialBalance
) {
}
