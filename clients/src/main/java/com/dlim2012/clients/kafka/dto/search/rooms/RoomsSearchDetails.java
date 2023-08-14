package com.dlim2012.clients.kafka.dto.search.rooms;

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
public class RoomsSearchDetails {
    private Integer roomsId;
    private Integer hotelId;
    private Long version;
//    private HotelSearchDetails hotelSearchDetails;
    private String displayName;
    private Integer maxAdult;
    private Integer maxChild;
    private Integer quantity;
    private Long priceMin;
    private Long priceMax;
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
    List<RoomDto> roomDto;
    List<PriceDto> priceDto;


    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoomDto {
        Long roomId;
        Long version;
        List<DatesDto> datesDtoList;
    }


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


    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DatesDto {
        Long datesId;
        Long roomId;
        LocalDate startDate;
        LocalDate endDate;
    }

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriceDto {
        Long priceId;
        LocalDate date;
        Long priceInCents;
    }


}
