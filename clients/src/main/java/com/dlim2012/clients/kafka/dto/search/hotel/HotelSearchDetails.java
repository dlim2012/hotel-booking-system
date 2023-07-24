package com.dlim2012.clients.kafka.dto.search.hotel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelSearchDetails {
    private Boolean createNew;

    private Integer id;
    private String name;
    private Integer propertyTypeOrdinal;
    private String neighborhood;
    private String zipcode;
    private String city;
    private String state;
    private String country;
    private Double latitude;
    private Double longitude;
    private Integer propertyRating;
    private List<FacilityDto> facility;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FacilityDto {
        Integer id;
        String displayName;
    }
}
