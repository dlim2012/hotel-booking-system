package com.dlim2012.clients.kafka.dto.search.dates;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DatesUpdateDetails {
    Integer hotelId;
    Map<Long, Long> datesVersions;
    Map<Long, List<DatesDto>> datesMap; // { room ID : { dates ID: dates } }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DatesDto{
        Long Id;
        LocalDate startDate;
        LocalDate endDate;
    }
}
