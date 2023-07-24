package com.dlim2012.clients.kafka.dto.search.price;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceDto {
    Long priceId;
    LocalDate date;
    Long priceInCents;
}
