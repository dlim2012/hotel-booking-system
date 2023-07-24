package com.dlim2012.clients.dto.booking;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomAvailabilityItem {
    @NotNull
    Integer roomId;
    @NotNull
    LocalDate localDate;
    @NotNull
    Integer quantity;
}
