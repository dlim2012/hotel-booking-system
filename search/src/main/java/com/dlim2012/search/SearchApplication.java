package com.dlim2012.search;

import com.dlim2012.clients.advice.ApplicationExceptionHandler;
import com.dlim2012.clients.elasticsearch.config.ElasticSearchClientConfiguration;
import com.dlim2012.clients.elasticsearch.service.ElasticSearchQuery;
import com.dlim2012.clients.security.config.AuthenticationConfig;
import com.dlim2012.clients.security.config.JwtAuthenticationFilter;
import com.dlim2012.clients.security.config.SecurityConfig;
import com.dlim2012.clients.security.dto.PublicKey;
import com.dlim2012.clients.security.dto.Roles;
import com.dlim2012.clients.security.service.JwtService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableConfigurationProperties({
		PublicKey.class,
		Roles.class
})
@Import(value = {
		// advice
		ApplicationExceptionHandler.class,
		// security
		SecurityConfig.class,
		AuthenticationConfig.class,
		JwtAuthenticationFilter.class,
		JwtService.class,
		// elastic search
		ElasticSearchQuery.class,
		ElasticSearchClientConfiguration.class
})
public class SearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(SearchApplication.class, args);
	}

}
