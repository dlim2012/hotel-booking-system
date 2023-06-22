package com.dlim2012.user;

import com.dlim2012.user.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminTokenRunner implements CommandLineRunner {

    private final JwtEncoder jwtEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("----------------------------------------------------------------- ADMIN TOKEN  -----------------------------------------------------------------------");
        Instant now = Instant.now();
        JwtClaimsSet claims1 = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.DAYS))
                .subject("junghoonlim12@gmail.com")
                .claim("id", "0")
                .claim("scope", "ADMIN")
                .build();
        System.out.println(this.jwtEncoder.encode(JwtEncoderParameters.from(claims1)).getTokenValue());
        System.out.println("----------------------------------------------------------------- USER TOKEN  -----------------------------------------------------------------------");
        JwtClaimsSet claims2 = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.DAYS))
                .subject("junghoonlim12@gmail.com")
                .claim("id", "0")
                .claim("scope", "APP_USER")
                .build();
        System.out.println(this.jwtEncoder.encode(JwtEncoderParameters.from(claims2)).getTokenValue());
        System.out.println("----------------------------------------------------------------- HOTEL TOKEN  -----------------------------------------------------------------------");
        JwtClaimsSet claims3 = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.DAYS))
                .subject("junghoonlim12@gmail.com")
                .claim("id", "0")
                .claim("scope", "HOTEL_MANAGER")
                .build();
        System.out.println(this.jwtEncoder.encode(JwtEncoderParameters.from(claims3)).getTokenValue());
        System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------");
    }
}
