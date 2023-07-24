package com.dlim2012.booking;

import com.dlim2012.clients.advice.ApplicationExceptionHandler;
import com.dlim2012.clients.cache.CacheConfig;
import com.dlim2012.clients.cache.booking.BookingCacheConfig;
import com.dlim2012.clients.cache.booking.BookingKeyGenerator;
import com.dlim2012.clients.kafka.config.KafkaConsumerConfig;
import com.dlim2012.clients.utils.PriceService;
import com.dlim2012.security.config.AuthenticationConfig;
import com.dlim2012.security.config.JwtAuthenticationFilter;
import com.dlim2012.security.config.SecurityConfig;
import com.dlim2012.security.dto.PublicKey;
import com.dlim2012.security.dto.Roles;
import com.dlim2012.security.service.JwtService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
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
		AuthenticationConfig.class,
		JwtAuthenticationFilter.class,
		JwtService.class,
		SecurityConfig.class,
//		//kafka
		KafkaConsumerConfig.class,
//		//cache
		CacheConfig.class,
		BookingKeyGenerator.class,
		BookingCacheConfig.class,
		//mysql
//		AvailableRoom.class,
//		Booking.class,
//		Room.class,
//		Invoice.class
		//utils
		PriceService.class
})
@EnableJpaRepositories({
		"com.dlim2012.clients.mysql_booking.repository"
})
@EntityScan({
		"com.dlim2012.clients.cassandra.entity",
		"com.dlim2012.clients.mysql_booking.entity"
})
@PropertySource("classpath:application-${spring.profiles.active}.yaml")
public class BookingApplication {
	public static void main(String[] args) {
		SpringApplication.run(BookingApplication.class, args);
	}
}
