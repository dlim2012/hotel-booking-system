package com.dlim2012.clients.security.dto;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "custom.security")
public class Roles {
    private final HashSet<String> roles;

    public boolean hasRole(String role){
        return roles.contains(role);
    }

    public HashSet<String> getRoles() {
        return roles;
    }
}
