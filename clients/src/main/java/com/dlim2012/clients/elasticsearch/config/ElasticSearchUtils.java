package com.dlim2012.clients.elasticsearch.config;

import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Configuration
public class ElasticSearchUtils {

    private final LocalDate DATE_SINCE = LocalDate.ofYearDay(1970, 1);

    public Integer toInteger(LocalDate localDate){
        return ((Long) ChronoUnit.DAYS.between(DATE_SINCE, localDate)).intValue();
    }

    public LocalDate toLocalDate(Integer integerDate){
        return DATE_SINCE.plusDays(integerDate);
    }

    public String getRoomId(String roomId, Integer integerDate){
        return roomId + "-" + integerDate.toString();
    }

    public String getRoomId(String roomId, LocalDate localDate){
        return getRoomId(roomId, toInteger(localDate));
    }
}
