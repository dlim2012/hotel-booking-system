package com.dlim2012.user.test_user;

import com.dlim2012.clients.entity.UserRole;
import com.dlim2012.user.dto.AuthenticationToken;
import com.dlim2012.user.entity.User;
import com.dlim2012.user.repository.UserRepository;
import com.dlim2012.user.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Random;

@RestController
@Slf4j
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@CrossOrigin
public class TestUserController {

    private final Random random = new Random();
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    private final TestUserService testUserService;

    public String randomString(int length){
        StringBuilder buffer = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomLimitedInt = 97 + (int)
                    (random.nextFloat() * (26));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }

    @PostMapping("/test-user")
    public AuthenticationToken generateTestUser() throws InterruptedException {
        log.info("Test user registration requested");

        String jwt = null;

        User user = null;
        for (int i=0; i<100; i++) {

            String email = "test-" + randomString(8) + "@email.com";

            if (userRepository.existsByEmail(email)){
                continue;
            }

            user = User.builder()
                    .firstName("John")
                    .lastName("Taylor")
                    .email(email)
                    .password(passwordEncoder.encode("password"))
                    .userRole(UserRole.APP_USER)
                    .userCreatedAt(LocalDateTime.now())
                    .locked(false)
                    .build();


            user = userRepository.save(user);

            jwt = tokenService.generateToken(user);
            break;
        }
        if (jwt == null){
            throw new RuntimeException("Failed to generate test user.");
        }

        testUserService.asyncMethod();
        testUserService.function(jwt);


        log.info("Test User registered with email {}", user.getEmail());
        return new AuthenticationToken(jwt);
    }


}
