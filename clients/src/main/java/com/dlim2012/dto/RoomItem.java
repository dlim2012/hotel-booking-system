package com.dlim2012.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record RoomItem(
        @NotNull(message = "Room id shouldn't be null.", groups = {RoomItem.SearchConsumerValidation.class})
        Integer id,
        @NotNull(message = "Room name shouldn't be null.", groups = {RoomItem.HotelValidation.class})
        String displayName,
        Boolean isActive,
        String description,
        @Positive(message = "Max adult should be positive.", groups = {RoomItem.HotelValidation.class})
        Integer maxAdult,
        @PositiveOrZero(message = "Max child should be positive or zero.", groups = {RoomItem.HotelValidation.class})
        Integer maxChild,
        @Positive(message = "Quantity should be a positive value.", groups = {RoomItem.HotelValidation.class})
        Integer quantity,
        @Positive(message = "Min price should be a positive value.", groups = {RoomItem.HotelValidation.class})
        Double priceMin,
        @Positive(message = "Max price should be a positive value.", groups = {RoomItem.HotelValidation.class})
        Double priceMax
) {

        public interface HotelValidation { }

        public interface SearchConsumerValidation {}

        public static RoomItem onlyId(Integer newId) {
                return new RoomItem(newId, null, null, null, null, null,
                        null, null, null);
        }

        public RoomItem withId(Integer newId){
                return new RoomItem(newId, displayName, isActive, description, maxAdult, maxChild, quantity, priceMin,
                        priceMax);
        }
}
