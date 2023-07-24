package com.dlim2012.search.dto.hotelSearch;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelSearchRequest {

    Boolean useRecommended;

    // address
    String city;
    String state;
    String country;
    Double latitude;
    Double longitude;

    // dates
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate startDate;
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate endDate;

    // size
    @NotNull
    Integer numAdult;
    @NotNull
    Integer numChild;
    Integer numBed;
    @NotNull
    Integer numRoom;

    // price
    Long priceMin;
    Long priceMax;

    // filters
    List<String> propertyTypes;
    List<Integer> propertyRating;
    List<String> hotelFacility;
    List<String> roomsFacility;
}
