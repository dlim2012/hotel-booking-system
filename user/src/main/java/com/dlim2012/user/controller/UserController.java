package com.dlim2012.user.controller;

import com.dlim2012.security.service.JwtService;
import com.dlim2012.user.dto.AuthenticationRequest;
import com.dlim2012.user.dto.AuthenticationToken;
import com.dlim2012.user.dto.UserRegisterRequest;
import com.dlim2012.user.dto.profile.UserProfileItem;
import com.dlim2012.user.service.TokenService;
import com.dlim2012.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@Slf4j
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {

    private final TokenService tokenService;
    private final UserService userService;
    private final JwtService jwtService;

    @GetMapping("/")
    public String home(Principal principal) {
        return "Hello" + (principal == null ? "" : ", " + principal.getName());
    }

    @PostMapping("/token")
    public String token(Authentication authentication){
        log.debug("Token requested for user: '{}'", authentication.getName());
        String token = tokenService.generateToken(authentication);
        log.debug("Token granted {}", token);
        return token;
    }

    @PostMapping("/register")
    public AuthenticationToken register(@RequestBody UserRegisterRequest userRegisterRequest){
        log.info("Register requested with email '{}'", userRegisterRequest.getEmail());
        String jwt = userService.register(userRegisterRequest);
        log.info("User registered with email '{}' ", userRegisterRequest.getEmail());
        return new AuthenticationToken(jwt);
    }

    @PostMapping("/login")
    public AuthenticationToken login(@RequestBody AuthenticationRequest authenticationRequest){
        log.info("Login requested from user '{}'", authenticationRequest.getEmail());
        String jwt = userService.authenticate(authenticationRequest);
        log.info("Login authorized to user '{}'", authenticationRequest.getEmail());
        return new AuthenticationToken(jwt);

    }

    @GetMapping("/profile")
    public UserProfileItem getProfile(){
        log.info("Get profile requested.");
        Jwt jwt = jwtService.getJwt();
        String userEmail = jwt.getSubject();
        Integer userId = jwtService.getId(jwt);
        return userService.getProfile(userId, userEmail);
    }

    @PostMapping("/profile/edit")
    public void editProfile(@RequestBody UserProfileItem userProfileItem){
        Integer userId = jwtService.getId();
        log.info("Profile edit request from user '{}'", userId);
        System.out.println(userProfileItem);


        userService.editProfile(userProfileItem, userId);
    }

//    @GetMapping("/contact/user/{userId}")
//    public UserContactInfo getContactInformation(
//            @PathVariable("userId") Integer userId
//    ){
//        log.info("Contact information of user {} requested.", userId);
//        return userService.getContactInfo(userId);
//    }
}
