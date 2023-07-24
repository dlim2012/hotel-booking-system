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
public class HotelSearchResponse {
    List<HotelSearchResponseItem> hotelList;
    Integer numResults;
    Long minPrice;
    Long maxPrice;
}
