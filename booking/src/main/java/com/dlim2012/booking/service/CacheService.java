package com.dlim2012.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import static com.dlim2012.clients.cache.CacheConfig.bookingIdKeyName;

@Service
@Slf4j
@RequiredArgsConstructor
public class CacheService {

//    @CachePut(cacheNames = "booking", keyGenerator = "bookingKeyGenerator")
//    public String cacheBookingForTTL(Booking booking){
//        log.info("Caching {} in redis", booking);
//        return "";
//    }
//
//    @CacheEvict(cacheNames = "booking", keyGenerator = "bookingKeyGenerator")
//    public void cacheBookingEvict(Booking booking){
//        log.info("Evicting {} in redis", booking);
//    }

    @CachePut(cacheNames = bookingIdKeyName)
    public String cacheBookingIdForTTL(Long bookingId){
        log.info("Caching {} in redis", bookingId);
        return "";
    }

    @CacheEvict(cacheNames = bookingIdKeyName)
    public void cacheBookingIdEvict(Long bookingId){
        log.info("Evicting bookingId {} in redis", bookingId);
    }
}
