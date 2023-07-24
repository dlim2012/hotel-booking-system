package com.dlim2012.hotel.dto.hotel.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelGeneralInfoItem {
    String name;
    String description;
    String propertyType;
    String phone;
    String fax;
    String website;
    String email;
    Integer propertyRating;
}
