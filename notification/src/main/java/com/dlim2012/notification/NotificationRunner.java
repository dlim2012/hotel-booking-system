package com.dlim2012.notification;

import com.dlim2012.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationRunner  implements CommandLineRunner {

    private final NotificationService notificationService;

    @Override
    public void run(String... args) throws Exception {
        notificationService.sendMail(
                "junghoonlim12@gmail.com",
                "Testing from Spring Boot",
                "Hello World from Spring Boot Email"
        );

    }
}
