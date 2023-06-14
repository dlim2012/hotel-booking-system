package com.dlim2012.search;

import com.dlim2012.ElasticSearchQuery;
import com.dlim2012.config.ElasticSearchClientConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(value = {ElasticSearchQuery.class, ElasticSearchClientConfiguration.class})
public class SearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(SearchApplication.class, args);
	}

}
