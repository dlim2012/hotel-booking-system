package com.dlim2012.test.dto.booking_management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingGuestInfoItem {

    private String firstName;
    private String lastName;
    private String email;
    private String specialRequests;
    private Integer estimatedArrivalHour;

    private List<Room> roomList;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Room {
        Integer roomsId;
        String roomsName;
        Long roomId;
        String guestName;
        String guestEmail;
    }

}
