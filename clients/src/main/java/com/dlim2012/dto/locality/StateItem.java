package com.dlim2012.dto.locality;

public record StateItem (
    Integer id,
    String name,
    String initials,
    String areaCode,
    Integer countryId
) {
}
