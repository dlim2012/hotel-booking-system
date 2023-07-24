package com.dlim2012.test.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterRequest {
        @NotNull
        String firstName;
        @NotNull
        String lastName;
        @Email
        String email;
        @NotNull
        String password;
}
