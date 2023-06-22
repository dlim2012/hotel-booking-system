package com.dlim2012.user.service;

import com.dlim2012.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtEncoder jwtEncoder;

    @Value("${custom.security.jwt.expire-time-in-minutes}")
    private Integer expireTimeInMinutes;

    public String generateToken(String username, Integer id, Collection<? extends GrantedAuthority> grantedAuthorities) {
        Instant now = Instant.now();
        String scope = grantedAuthorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.MINUTES))
                .subject(username)
                .claim("id", id.toString())
                .claim("scope", scope)
                .build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public String generateToken(User user) {
        return generateToken(user.getUsername(), Math.toIntExact(user.getId()), user.getAuthorities());
    }

    public String generateToken(Authentication authentication) {
        return generateToken(authentication.getName(), -1, authentication.getAuthorities());
    }




}
