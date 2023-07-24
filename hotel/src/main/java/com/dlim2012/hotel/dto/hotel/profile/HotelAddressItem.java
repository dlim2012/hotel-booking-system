package com.dlim2012.hotel.dto.hotel.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelAddressItem {
    String addressLine1;
    String addressLine2;
    String neighborhood;
    String city;
    String state;
    String country;
    String zipcode;
    Double latitude;
    Double longitude;
}
