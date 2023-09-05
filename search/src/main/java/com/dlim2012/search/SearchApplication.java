package com.dlim2012.search;

import com.dlim2012.clients.advice.ApplicationExceptionHandler;
import com.dlim2012.clients.elasticsearch.config.ElasticSearchUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })

@Import(value = {
		// advice
		ApplicationExceptionHandler.class,
		// elastic search
		ElasticSearchUtils.class
})
@PropertySource("classpath:application-${spring.profiles.active}.yaml")
public class SearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(SearchApplication.class, args);
	}

}
