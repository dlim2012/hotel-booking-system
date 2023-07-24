package com.dlim2012.user.test_user.dto.search;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PriceAggResponse {
    Integer roomsId;
    Long sumPrice;
}
