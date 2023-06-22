package com.dlim2012.bookingmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDetails {
    String hotel;
    String room;
    String addressLine1;
    String addressLine2;
    String zipcode;
    String city;
    String state;
    String country;
    String displayImageUrl;
}
