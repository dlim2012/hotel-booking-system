package com.dlim2012.clients.kafka.dto.booking.rooms;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomsBookingDetails {
    private Integer roomsId;
    private Integer hotelId;
    private Integer hotelManagerId;
    //    private HotelSearchDetails hotelSearchDetails;
    private String displayName;
    private String shortName;
    private Integer maxAdult;
    private Integer maxChild;
    private Integer quantity;
    private Integer numBed;
    private Boolean breakfast;
    private Long priceMax;
    private Long priceMin;
    private Integer checkInTime;
    private Integer checkOutTime;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate availableFrom;
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate availableUntil;
    private Integer freeCancellationDays;
    private Integer noPrepaymentDays;
    List<FacilityDto> facilityDto;
    List<BedInfoDto> bedDto;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BedInfoDto {
        Integer id;
        String size;
        Integer quantity;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FacilityDto {
        Integer id;
        String displayName;
    }
}
