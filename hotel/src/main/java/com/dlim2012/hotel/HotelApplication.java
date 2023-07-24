package com.dlim2012.hotel;

import com.dlim2012.clients.advice.ApplicationExceptionHandler;
import com.dlim2012.security.config.AuthenticationConfig;
import com.dlim2012.security.config.JwtAuthenticationFilter;
import com.dlim2012.security.config.SecurityConfig;
import com.dlim2012.security.dto.PublicKey;
import com.dlim2012.security.dto.Roles;
import com.dlim2012.security.service.JwtService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.dlim2012.clients")
@EnableKafka
@EnableConfigurationProperties({
        PublicKey.class,
        Roles.class
})
@Import(value = {
        // security
        AuthenticationConfig.class,
        JwtAuthenticationFilter.class,
        JwtService.class,
        SecurityConfig.class,
        // advice
        ApplicationExceptionHandler.class
        // kafka

})
@PropertySource("classpath:application-${spring.profiles.active}.yaml")
public class HotelApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelApplication.class, args);
    }


}
