package com.dlim2012.clients.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelItem {
        private Boolean isActive;
        private Integer id;
        private String name;
        private String description;
        private String propertyType;
        private String addressLine1;
        private String addressLine2;
        private String zipcode;
        private String city;
        private String state;
        private String country;
        private Double latitude;
        private Double longitude;
        private Integer propertyRating;
        private List<String> facilityDisplayNameList;

}
