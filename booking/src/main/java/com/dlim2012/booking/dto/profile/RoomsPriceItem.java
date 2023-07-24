package com.dlim2012.booking.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomsPriceItem {
    Integer roomsId;
    List<PriceDto> priceDtoList;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriceDto {
        LocalDate date;
        Long priceInCents;

    }
}
