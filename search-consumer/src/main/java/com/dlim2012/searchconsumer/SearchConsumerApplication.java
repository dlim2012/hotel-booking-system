package com.dlim2012.searchconsumer;

import com.dlim2012.clients.elasticsearch.config.ElasticSearchClientConfiguration;
import com.dlim2012.clients.elasticsearch.config.ElasticSearchUtils;
//import com.dlim2012.clients.elasticsearch.service.ElasticSearchQuery;
import com.dlim2012.clients.kafka.config.KafkaConsumerConfig;
import com.dlim2012.clients.utils.PriceService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
@EnableKafka
@Import(value = {
		// elastic search
//		ElasticSearchQuery.class,
		ElasticSearchClientConfiguration.class,
		ElasticSearchUtils.class,
//		UnsafeX509ExtendedTrustManager.class,
		// kafka
		KafkaConsumerConfig.class,
		// utils
		PriceService.class,
		})
@PropertySource("classpath:application-${spring.profiles.active}.yaml")
public class SearchConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SearchConsumerApplication.class, args);
	}

}
