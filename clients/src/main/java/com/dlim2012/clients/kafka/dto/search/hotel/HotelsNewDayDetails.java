package com.dlim2012.clients.kafka.dto.search.hotel;

import com.dlim2012.clients.kafka.dto.search.dates.DatesUpdateDetails;
import com.dlim2012.clients.kafka.dto.search.price.PriceDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelsNewDayDetails {

    Integer startId;
    Integer endId;
    Map<Integer, Long> hotelVersionMap;
    Map<Integer, DatesUpdateDetails> datesUpdateDetailsMap; // { hotelId: datesUpdateDetails }
    Map<Integer, Map<Integer, List<PriceDto>>> priceUpdateDetailsMap; // { hotelId: { roomsId: [price] } }
//    Map<Integer, >

//

}
