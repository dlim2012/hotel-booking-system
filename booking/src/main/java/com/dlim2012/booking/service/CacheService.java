package com.dlim2012.booking.service;

import com.dlim2012.clients.mysql_booking.entity.Booking;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    @CachePut(cacheNames = "booking", keyGenerator = "bookingKeyGenerator")
    public String cacheBookingForTTL(Booking booking){
        return "";
    }

    @CacheEvict(cacheNames = "booking", keyGenerator = "bookingKeyGenerator")
    public void cacheBookingEvict(Booking booking){}
}
