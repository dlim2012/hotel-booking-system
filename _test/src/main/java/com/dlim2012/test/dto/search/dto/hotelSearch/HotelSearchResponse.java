package com.dlim2012.test.dto.search.dto.hotelSearch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelSearchResponse {
    Integer hotelId;
    String hotelName;
    String propertyType;
    String neighborhood;
    String city;
    Double distance;
    Integer numRoom;
    Long totalPrice;
    Integer maxFreeCancellationDays;
    Integer noPrepaymentDays;
    Boolean breakfast;
    List<HotelSearchRooms> roomsList;
    Double score;
}
