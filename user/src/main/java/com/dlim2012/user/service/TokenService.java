package com.dlim2012.user.service;

import com.dlim2012.user.entity.User;
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
//@RequiredArgsConstructor
public class TokenService {
    private final JwtEncoder jwtEncoder;

//    @Value("${custom.security.jwt.expire-time-in-minutes}")
    private final Integer expireTimeInMinutes;

    public TokenService(
            JwtEncoder jwtEncoder,
            @Value("${custom.security.jwt.expire-time-in-minutes}") Integer expireTimeInMinutes) {
        System.out.println("Token expire time in minutes: " + expireTimeInMinutes.toString());
        this.jwtEncoder = jwtEncoder;
        this.expireTimeInMinutes = expireTimeInMinutes;
    }

    public String generateToken(String username, String userDisplayName, Integer id, Collection<? extends GrantedAuthority> grantedAuthorities) {
        Instant now = Instant.now();
        String scope = grantedAuthorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(expireTimeInMinutes, ChronoUnit.MINUTES))
                .subject(username)
                .claim("id", id.toString())
                .claim("scope", scope)
                .claim("displayName", userDisplayName)
                .build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public String generateToken(User user) {
        return generateToken(
                user.getEmail(),
                user.getDisplayName() == null ? user.getFirstName() : user.getDisplayName(),
                Math.toIntExact(user.getId()),
                user.getAuthorities());
    }

    public String generateToken(Authentication authentication) {
        return generateToken(authentication.getName(), authentication.getName(), -1, authentication.getAuthorities());
    }




}
