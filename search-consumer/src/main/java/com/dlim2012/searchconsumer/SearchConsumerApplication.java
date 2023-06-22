package com.dlim2012.searchconsumer;

import com.dlim2012.clients.elasticsearch.config.ElasticSearchClientConfiguration;
import com.dlim2012.clients.elasticsearch.service.ElasticSearchQuery;
import com.dlim2012.clients.kafka.config.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
@EnableKafka
@Import(value = {
		// elastic search
		ElasticSearchQuery.class,
		ElasticSearchClientConfiguration.class,
		// kafka
		KafkaConsumerConfig.class,
		KafkaHotelConsumerConfig.class,
		KafkaRoomConsumerConfig.class,
		KafkaFacilityConsumerConfig.class,
		KafkaHotelFacilitiesConsumerConfig.class,
		KafkaRoomFacilitiesConsumerConfig.class,
		KafkaBookingConsumerConfig.class,
		})
public class SearchConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SearchConsumerApplication.class, args);
	}

}
