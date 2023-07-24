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
public class HotelSearchRooms {
    Integer roomsId;
    String displayName;
    Integer maxAdult;
    Integer maxChild;
    Integer numBed;
    Integer recommended;
    Integer quantity;
    Long price;
    List<BedInfo> bedInfoList;
}
