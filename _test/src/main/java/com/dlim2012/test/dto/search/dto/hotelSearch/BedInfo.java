package com.dlim2012.test.dto.search.dto.hotelSearch;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BedInfo {
    String size;
    Integer quantity;
}