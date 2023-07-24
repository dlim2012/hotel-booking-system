package com.dlim2012.bookingmanagement.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActiveBookingItem {
    Long id;
    Integer userId;
    Integer hotelId;
    String hotelName; //
    LocalDateTime reservationTime;
    String address; //
    String firstName;
    String lastName;
    String email;
    String specialRequests;
    Integer estimatedArrivalHour;
    LocalDateTime startDateTime; //
    LocalDateTime endDateTime; //
    String status; //
    Long priceInCents;
    String invoiceId; //
    LocalDateTime invoiceConfirmTime;
    List<Room> room;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Room {
        Long bookingRoomsId;
        Long bookingRoomId;
        Integer roomsId;
        Long roomId;
        String roomsDisplayName;
        LocalDate prepayUntil;
        LocalDate freeCancellationUntil;
        Long priceInCents;
        LocalDateTime startDateTime;
        LocalDateTime endDateTime;
        String status;
        String guestName;
        String guestEmail;

    }

    public void setAddress(String neighborhood, String city, String state, String country){
        String address = "";
        if (neighborhood!= null && !neighborhood.isEmpty()){
            address += neighborhood+ ", ";
        }
        if (city != null && !city.isEmpty()) {
            address += city + ", ";
        }
        if (state != null && !state.isEmpty()) {
            address += state + ", ";
        }
        address += country;
        this.setAddress(address);
    }
}
