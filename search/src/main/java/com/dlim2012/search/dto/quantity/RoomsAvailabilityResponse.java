package com.dlim2012.search.dto.quantity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomsAvailabilityResponse {

    Map<Integer, Rooms> rooms;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Rooms{
        Integer roomsId;
        Integer quantity;
        Long price;

    }
}
