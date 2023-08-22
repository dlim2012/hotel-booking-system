package com.dlim2012.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//    private PasswordEncoder passwordEncoder;

//    @Bean
//    public InMemoryUserDetailsManager user() {
//        return new InMemoryUserDetailsManager(
//                User.withUsername("admin")
////                        .passwordEncoder(passwordEncoder::encode)
//                        .password("{noop}password")
//                        .authorities("read")
//                        .build()
//        );
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
//                        .anyRequest().permitAll()
                                .requestMatchers(
                                        "/api/v1/hotel/image/*",
//                                        "/api/v1/hotel/hotel/*/rooms",
                                        "/api/v1/hotel/public/**",
                                        "/api/v1/search/hotel",
                                        "/api/v1/search/price",
                                        "/api/v1/booking/public/**",
                                        "/api/v1/booking/payment/**",
                                        "/api/v1/booking/redirect",
                                        "/api/v1/hotel/test",
                                        "/api/v1/booking/test",
                                        "/api/v1/search/test",
                                        "/api/v1/booking-management/test",
                                        "/api/v1/hotel/test/**",
                                        "/api/v1/booking/test/**",
                                        "/api/v1/search/test/**",
                                        "/api/v1/search-consumer/test/**"
                                        ).permitAll()
                                .anyRequest().authenticated()
                )
//                .oauth2ResourceServer((oauth2) -> oauth2.jwt(
//                        Customizer.withDefaults()))
                .addFilterAfter(jwtAuthenticationFilter, BearerTokenAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(Customizer.withDefaults())
                .build();
    }

}
