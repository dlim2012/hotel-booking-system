package com.dlim2012.clients.cache.booking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@RequiredArgsConstructor
public class BookingKeyGenerator implements KeyGenerator {

    private final ObjectMapper objectMapper;

    @Override
    public Object generate(Object target, Method method, Object... params) {
        try {
            return objectMapper.writeValueAsString(params);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
//        return StringUtils.arrayToDelimitedString(params, "_");
    }
}
