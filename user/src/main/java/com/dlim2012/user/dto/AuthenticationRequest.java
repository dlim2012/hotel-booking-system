package com.dlim2012.user.dto;

public record AuthenticationRequest (
        String email,
        String password
) {
}
