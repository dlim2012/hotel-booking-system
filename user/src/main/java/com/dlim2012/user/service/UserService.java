package com.dlim2012.user.service;

import com.dlim2012.user.dto.AuthenticationRequest;
import com.dlim2012.user.dto.RegisterRequest;
import com.dlim2012.user.entity.User;
import com.dlim2012.clients.entity.UserRole;
import com.dlim2012.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public String register(RegisterRequest registerRequest){
        UserRole userRole = UserRole.valueOf(registerRequest.userRole());
        if (userRole == UserRole.ADMIN){
            throw new IllegalArgumentException("Invalid user role.");
        }

        if (userRepository.existsByEmail(registerRequest.email())){
            throw new IllegalArgumentException("Email already exists.");
        }

        User user = User.builder()
                .firstName(registerRequest.firstName())
                .lastName(registerRequest.lastName())
                .email(registerRequest.email())
                .password(passwordEncoder.encode(registerRequest.password()))
                .userRole(userRole)
                .userCreatedAt(LocalDateTime.now())
                .locked(false)
                .build();
        userRepository.save(user);
        return tokenService.generateToken(user);
    }

    public String authenticate(AuthenticationRequest authenticationRequest){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.email(),
                        authenticationRequest.password()
                )
        );

        // user is manually set into SecurityContextHolder during authentication
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return tokenService.generateToken(user);
    }
}
