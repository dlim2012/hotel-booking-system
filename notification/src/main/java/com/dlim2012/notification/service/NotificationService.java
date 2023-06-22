package com.dlim2012.notification.service;

import com.dlim2012.clients.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.mail.MailSender;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final MailSender mailSender;

    public void sendMail(String to, String subject, String text){
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(text);
        mailSender.send(msg);
        System.out.println("mail sent!");
    }

}
