package com.dlim2012.user.controller;

import com.dlim2012.user.dto.UserContactInfoRequest;
import com.dlim2012.user.dto.UserContactInfoResponse;
import com.dlim2012.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/user/notification")
@RequiredArgsConstructor
@CrossOrigin
public class NotificationController {

    private final UserService userService;

    @PostMapping("/contact-info")
    public List<UserContactInfoResponse> getUserContactInfo(
            List<UserContactInfoRequest> request
    ){
        log.info("Contact info requested: {}", request);
        return userService.getContactInfo(request);
    }
}
