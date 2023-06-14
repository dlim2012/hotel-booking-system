package com.dlim2012.dto;

import jakarta.validation.constraints.NotNull;

public record HotelFullAddressItem(
        @NotNull(message = "Hotel id shouldn't be null.", groups = {SearchConsumerValidation.class})
        Integer id,
        @NotNull(message = "Hotel name shouldn't be null.", groups = {HotelValidation.class})
        String name,
        String description,
        Boolean isActive,
        @NotNull(message = "Address line 1 shouldn't be null.", groups = {HotelValidation.class})
        String addressLine1,
        String addressLine2,
        String zipCode,
        @NotNull(message = "City name shouldn't be null.", groups = {HotelValidation.class})
        String cityName,
        String stateName,
        @NotNull(message = "Country name shouldn't be null.", groups = {HotelValidation.class})
        String countryName
) {

        public interface HotelValidation { }

        public interface SearchConsumerValidation {}

        public static HotelFullAddressItem onlyId(Integer newId){
                return new HotelFullAddressItem(
                        newId, null, null, null, null, null,
                        null, null, null, null
                );
        }

        public HotelFullAddressItem withId(Integer newId){
                return new HotelFullAddressItem(
                        newId, name, description, isActive, addressLine1, addressLine2, zipCode, cityName, stateName,
                        countryName
                );
        }

}
