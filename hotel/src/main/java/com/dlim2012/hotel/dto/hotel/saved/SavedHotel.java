package com.dlim2012.hotel.dto.hotel.saved;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedHotel {
    Integer id;
    String name;
    String address;
    // main image url
}
