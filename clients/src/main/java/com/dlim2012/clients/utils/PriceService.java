package com.dlim2012.clients.utils;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static java.lang.Math.max;
import static java.lang.Math.min;

@Service
public class PriceService {

    private final Integer MAX_BOOKING_DAYS = 90;

    public Long getPriceSumInCents(LocalDate startDate, LocalDate endDate, Long priceMax, Long priceMin, Integer quantity){
        Long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        LocalDate today = LocalDate.now();
        Long averagePrice = priceMin + (priceMax - priceMin) * (getDayDiff(today, startDate) + getDayDiff(today, endDate)) / 2 / MAX_BOOKING_DAYS;
        double price = days * averagePrice * quantity;
        return (Long) (long) (price * 100);
    }

    public Long getPriceInCents(
            Long dayDiff, Long priceMax, Long priceMin
    ){
        return priceMin + (priceMax - priceMin) * dayDiff / MAX_BOOKING_DAYS;
    }

    public Long getPriceInCents(
            LocalDate today, LocalDate localDate, Long priceMax, Long priceMin
    ){
        return getPriceInCents(getDayDiff(today, localDate), priceMax, priceMin);
    }

    public Long getDayDiff(
            LocalDate today, LocalDate localDate
    ){
        return min(MAX_BOOKING_DAYS, max(0, ChronoUnit.DAYS.between(today, localDate)));
    }

    public Long changeDoubleToCents(
            Double value
    ){
        return Math.round(value * 100);
    }
}
