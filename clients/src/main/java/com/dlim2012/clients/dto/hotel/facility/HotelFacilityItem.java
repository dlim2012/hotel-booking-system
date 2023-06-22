package com.dlim2012.clients.dto.hotel.facility;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelFacilityItem {
    Integer id;
    @Null(groups = {Put.class})
    Integer hotelId;
    @NotNull(groups = {Put.class})
    Integer facilityId;
    @NotNull(groups = {Put.class})
    Boolean isActive;

    public interface Put {}
}
