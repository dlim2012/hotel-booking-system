package com.dlim2012.clients.kafka.dto.search.rooms;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomsSearchVersion {
    Integer roomsId;
    Integer hotelId;
    Integer freeCancellationDays;
    Integer noPrepaymentDays;
    Long version;
    List<RoomDto> roomDto;
    List<PriceDto> priceDto;


    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoomDto {
        Long roomId;
        Long version;
        List<RoomsSearchDetails.DatesDto> datesDtoList;
    }

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DatesDto {
        Long datesId;
        Long roomId;
        LocalDate startDate;
        LocalDate endDate;
    }

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriceDto {
        Long priceId;
        LocalDate date;
        Long priceInCents;
    }

}
