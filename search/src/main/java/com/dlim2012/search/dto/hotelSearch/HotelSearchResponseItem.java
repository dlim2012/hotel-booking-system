package com.dlim2012.search.dto.hotelSearch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelSearchResponseItem {
    Integer hotelId;
    String hotelName;
    String propertyType;
    String neighborhood;
    String city;
    String state;
    String zipcode;
    Double distance;
    Integer numRoom;
    Long totalPrice;
    Integer maxFreeCancellationDays;
    Integer noPrepaymentDays;
    Boolean breakfast;
    List<HotelSearchRooms> roomsList;
    Double score;
}
