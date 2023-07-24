package com.dlim2012.booking.dto.reserve;


import com.fasterxml.jackson.annotation.JsonProperty;
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
public class BookingRequest {
    String firstName;
    String lastName;
    String email;
    String hotelName;
    String neighborhood;
    String city;
    String state;
    String country;
    LocalDate startDate;
    LocalDate endDate;
    Integer checkInTime;
    Integer checkOutTime;
    String specialRequests;
    Integer estimatedArrivalHour;
    Long priceInCents;
    @JsonProperty("rooms")
    List<BookingRequestRooms> rooms;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BookingRequestRooms {
        Integer roomsId;
        String roomsName;
        String guestName;
        String guestEmail;
        LocalDate noPrepaymentUntil;
        LocalDate freeCancellationUntil;
    }
}
