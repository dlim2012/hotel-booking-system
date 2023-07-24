package com.dlim2012.user;

import com.dlim2012.clients.advice.ApplicationExceptionHandler;
import com.dlim2012.security.service.JwtService;
import com.dlim2012.user.config.RsaKeys;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@EnableConfigurationProperties(RsaKeys.class)
@Import({
		// advice
		ApplicationExceptionHandler.class,
		// security
		JwtService.class
})
@CrossOrigin
@PropertySource("classpath:application-${spring.profiles.active}.yaml")
public class UserApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserApplication.class, args);
	}

}
