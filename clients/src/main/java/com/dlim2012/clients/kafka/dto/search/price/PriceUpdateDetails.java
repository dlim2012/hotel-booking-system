package com.dlim2012.clients.kafka.dto.search.price;

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
public class PriceUpdateDetails {
    Integer hotelId;
    Integer roomsId;
    List<PriceDto> priceDtoList;

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriceDto {
        Long priceId;
        LocalDate date;
        Long priceInCents;
        Integer version;
    }

}
