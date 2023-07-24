package com.dlim2012.search.dto.count;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NumberByCityRequest {
    String city;
    String state;
    String country;
}
