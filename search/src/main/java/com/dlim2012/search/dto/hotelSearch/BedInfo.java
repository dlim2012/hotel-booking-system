package com.dlim2012.search.dto.hotelSearch;


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