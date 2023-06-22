package com.dlim2012.user.controller;

import com.dlim2012.user.dto.AuthenticationRequest;
import com.dlim2012.user.dto.AuthenticationToken;
import com.dlim2012.user.dto.RegisterRequest;
import com.dlim2012.user.service.TokenService;
import com.dlim2012.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {


    private final TokenService tokenService;
    private final UserService userService;


    @PostMapping("/token")
    public String token(Authentication authentication){
        log.debug("Token requested for user: '{}'", authentication.getName());
        String token = tokenService.generateToken(authentication);
        log.debug("Token granted {}", token);
        return token;
    }

    @PostMapping("/register")
    public AuthenticationToken register(@RequestBody @Valid RegisterRequest registerRequest){
        log.info("Register requested with email '{}'", registerRequest.email());
        String jwt = userService.register(registerRequest);
        log.info("User registered with email '{}' ", registerRequest.email());
        return new AuthenticationToken(jwt);

    }

    @PostMapping("/login")
    public AuthenticationToken login(@RequestBody AuthenticationRequest authenticationRequest){
        log.info("Login requested from user '{}'", authenticationRequest.email());
        String jwt = userService.authenticate(authenticationRequest);
        log.info("Login authorized to user '{}'", authenticationRequest.email());
        return new AuthenticationToken(jwt);

    }


    // todo: set authorization scope accordingly (hotelmanager, user)
    // todo: remove password login
}
