package com.dlim2012.bookingmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingHistoryResponse {
    Long bookingId;
    Integer hotelId;
    Integer userId;
    String hotelName;
    String neighborhood;
    String city;
    String state;
    String country;
    LocalDateTime startDateTime;
    LocalDateTime endDateTime;
    Long priceInCents;
    String invoiceId;
    LocalDateTime invoiceConfirmationTime;
    String status;
    List<Rooms> rooms;

    class Rooms {
        Integer roomsId;
        String roomsName;
        Integer quantity;
    }
}
