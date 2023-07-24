package com.dlim2012.booking.dto.availability;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelAvailabilityResponse {

    Map<Integer, RoomsAvailability> roomsAvailability;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoomsAvailability {
        Integer quantity;
        Long priceInCents;
    }
}
