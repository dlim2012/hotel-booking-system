package com.dlim2012.search.dto.priceAgg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PriceAggRequest {
    Integer hotelId;
    LocalDate startDate;
    LocalDate endDate;
}
