package com.dlim2012.clients.security.config;

import com.dlim2012.clients.security.dto.Roles;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final Roles roles;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest httpServletRequest,
        @NonNull HttpServletResponse httpServletResponse,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {final String authHeader = httpServletRequest.getHeader("Authorization");
        Object principal;
        try {
            principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e){
            return;
        }
        if (principal.getClass().equals(Jwt.class)){
            Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String[] scopes = ((String) jwt.getClaims().get("scope")).split(" ");

            // set scopes in authentication
            Authentication authentication = new JwtAuthenticationToken(
                    jwt,
                    Arrays.stream(scopes).map(SimpleGrantedAuthority::new).toList()
                    );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // continue only if a scope is allowed
            for (String role: scopes){
                if (roles.hasRole(role)){
                    filterChain.doFilter(httpServletRequest, httpServletResponse);
                    return;
                    }
                }
        }
        SecurityContextHolder.getContext().setAuthentication(null);
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
