package com.dlim2012.clients.security.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JwtService {

    public Jwt getJwt(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!principal.getClass().equals(Jwt.class)){
            throw new IllegalStateException("The authentication is not a JWT Authentication.");
        }
        return (Jwt) principal;

    }

    public Integer getId(){
        return Integer.parseInt((String) getJwt().getClaims().get("id"));
    }

    public String getEmail(){
        return getJwt().getSubject();
    }
}
