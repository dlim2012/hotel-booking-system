package com.dlim2012.bookingmanagement.dto.hotelInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelDatesInfoResponse {
//    Map<Integer, Map<Long, Room>> roomsMap;
    Map<Long, Room> roomMap;


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Room{
        Integer roomsId;
        String title;
        Boolean isActive;
        List<Dates> dates;
        LocalDate availableFrom;
        LocalDate availableUntil;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Dates{
        Long bookingId;
        Long datesId;
        Long bookingRoomsId;
        Long bookingRoomId;
        String status; // "AVAILABLE, RESERVED, BOOKED"
        LocalDateTime startDateTime;
        LocalDateTime endDateTime;
    }


}
