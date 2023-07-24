package com.dlim2012.hotel.dto.hotel.list;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelRowItem {
    Integer id;
    String name;
    String address;
}
