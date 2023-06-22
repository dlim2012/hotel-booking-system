package com.dlim2012.booking;

import com.dlim2012.clients.advice.ApplicationExceptionHandler;
import com.dlim2012.clients.cache.CacheConfig;
import com.dlim2012.clients.cache.booking.BookingCacheConfig;
import com.dlim2012.clients.cache.booking.BookingKeyGenerator;
import com.dlim2012.clients.kafka.config.KafkaConsumerConfig;
import com.dlim2012.clients.kafka.config.KafkaRoomConsumerConfig;
import com.dlim2012.clients.security.config.AuthenticationConfig;
import com.dlim2012.clients.security.config.JwtAuthenticationFilter;
import com.dlim2012.clients.security.config.SecurityConfig;
import com.dlim2012.clients.security.dto.PublicKey;
import com.dlim2012.clients.security.dto.Roles;
import com.dlim2012.clients.security.service.JwtService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
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
//		//kafka
		KafkaConsumerConfig.class,
		KafkaRoomConsumerConfig.class,
//		//cache
		CacheConfig.class,
		BookingKeyGenerator.class,
		BookingCacheConfig.class,
		//mysql
//		AvailableRoom.class,
//		Booking.class,
//		Room.class,
//		Invoice.class
})
@EnableJpaRepositories({
		"com.dlim2012.clients.mysql_booking.repository"
})
@EntityScan({
		"com.dlim2012.clients.cassandra.entity",
		"com.dlim2012.clients.mysql_booking.entity"
})
public class BookingApplication {
	public static void main(String[] args) {
		SpringApplication.run(BookingApplication.class, args);
	}


}
