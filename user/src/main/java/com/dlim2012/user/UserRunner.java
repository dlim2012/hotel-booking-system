package com.dlim2012.user;

import com.dlim2012.clients.entity.UserRole;
import com.dlim2012.user.entity.User;
import com.dlim2012.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRunner implements CommandLineRunner {

    private final JwtEncoder jwtEncoder;

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        try {
            createUser(1);
        } catch (Exception ignored){

        }
        try{
            createAppUser();
        } catch (Exception ignored){

        }

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

    public void createUser(Integer id){

        User admin = User.builder()
                .id(id)
                .firstName("admin")
                .lastName("admin")
                .email("admin@hb.com")
                .userCreatedAt(LocalDateTime.now())
                .locked(false)
                .userRole(UserRole.ADMIN)
                .password(passwordEncoder.encode( "admin_user_password"))
                .dateOfBirth(LocalDate.now())
                .build();
        userRepository.save(admin);

    }

    public void createAppUser(){


        User appUser = User.builder()

                .firstName("James")
                .lastName("Anderson")
                .email("appUser@hb.com")
                .userCreatedAt(LocalDateTime.now())
                .locked(false)
                .userRole(UserRole.APP_USER)
                .password(this.passwordEncoder.encode( "app_user_password"))
                .dateOfBirth(LocalDate.now())
                .build();
        userRepository.save(appUser);
    }
}
