package com.dlim2012.clients.kafka.dto.search.dates;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DatesUpdateDetails {
    Integer hotelId;
    Map<Long, Map<Long, DatesDto>> datesToUpdate; // { room ID : { dates ID: dates } }
    Map<Long, Set<Long>> datesIdsToDelete; // { room ID: { dates ID: dates } }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DatesDto{
        LocalDate startDate;
        LocalDate endDate;
    }
}
