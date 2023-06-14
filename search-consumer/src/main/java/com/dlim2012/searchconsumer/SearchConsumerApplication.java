package com.dlim2012.searchconsumer;

import com.dlim2012.ElasticSearchQuery;
import com.dlim2012.config.ElasticSearchClientConfiguration;
import com.dlim2012.config.KafkaConsumerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.dlim2012.clients")
@EnableKafka
@Import(value = {ElasticSearchQuery.class, ElasticSearchClientConfiguration.class,
		KafkaConsumerConfig.class})
public class SearchConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SearchConsumerApplication.class, args);
	}

}
