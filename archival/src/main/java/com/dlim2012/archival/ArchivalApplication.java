package com.dlim2012.archival;

import com.dlim2012.clients.advice.ApplicationExceptionHandler;
import com.dlim2012.clients.cassandra.config.CassandraConfig;
import com.dlim2012.clients.kafka.config.KafkaBookingConsumerConfig;
import com.dlim2012.clients.kafka.config.KafkaConsumerConfig;
import com.dlim2012.clients.kafka.config.KafkaRoomConsumerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication//(exclude={DataSourceAutoConfiguration.class})
@EnableKafka
@Import(value = {
		// advice
		ApplicationExceptionHandler.class,
		// kafka
		KafkaConsumerConfig.class,
		KafkaRoomConsumerConfig.class,
		KafkaBookingConsumerConfig.class,
		// cassandra
		CassandraConfig.class
})
@EnableCassandraRepositories({
		"com.dlim2012.clients.cassandra.repository"})
@EnableJpaRepositories({
		"com.dlim2012.clients.mysql_booking.repository"
})
@EntityScan({
		"com.dlim2012.clients.cassandra.entity",
		"com.dlim2012.clients.mysql_booking.entity"
})
public class ArchivalApplication {

	public static void main(String[] args) {
		SpringApplication.run(ArchivalApplication.class, args);
	}

}
