package com.dlim2012.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest (
        @NotNull
        String firstName,
        @NotNull
        String lastName,
        @Email
        String email,
        @NotNull
        String password,
        @NotNull
        String userRole
) {
}
