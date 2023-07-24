package com.dlim2012.clients.kafka.dto.search.price;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PriceUpdateDetails {
    Integer hotelId;
    Integer roomsId;
    List<PriceDto> priceDtoList;

}
