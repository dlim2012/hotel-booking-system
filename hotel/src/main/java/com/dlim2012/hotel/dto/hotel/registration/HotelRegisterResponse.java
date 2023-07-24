package com.dlim2012.hotel.dto.hotel.registration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelRegisterResponse {
    Integer id;
    String name;
    String address;
}
