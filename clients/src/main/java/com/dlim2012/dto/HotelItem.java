package com.dlim2012.dto;

import jakarta.validation.constraints.NotNull;

public record HotelItem(
        Integer id,
        @NotNull(message = "Hotel name shouldn't be null.")
        String name,
        String description,
        Boolean isActive,
        @NotNull(message = "Address line 1 shouldn't be null.")
        String addressLine1,
        String addressLine2,
        Integer localityId
) {
}
