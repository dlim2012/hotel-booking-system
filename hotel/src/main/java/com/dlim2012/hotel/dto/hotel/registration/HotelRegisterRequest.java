package com.dlim2012.hotel.dto.hotel.registration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelRegisterRequest {
    @NotBlank
    String name;
    String description;
    String propertyType;
    @NotBlank
    String addressLine1;
    String addressLine2;
    String neighborhood;
    String zipcode;
    String city;
    String state;
    @NotBlank
    String country;
    @NotNull
    Double latitude;
    @NotNull
    Double longitude;
//    Double distanceFromCenter;
    private String phone;
    private String fax;
    private String website;
    private String email;
    private Integer propertyRating;
    List<String> facilityDisplayNameList;
}
