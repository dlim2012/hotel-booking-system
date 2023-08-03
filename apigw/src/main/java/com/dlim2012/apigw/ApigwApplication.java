package com.dlim2012.apigw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:application-${spring.profiles.active}.yaml")
public class ApigwApplication {
	public static void main(String[] args) {
		SpringApplication.run(ApigwApplication.class, args);
	}

}
