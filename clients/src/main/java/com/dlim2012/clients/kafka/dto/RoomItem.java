package com.dlim2012.clients.kafka.dto;

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
public class RoomItem {
        private Boolean isActive;
        private Integer id;
        private Integer hotelId;
        private String displayName;
        private Integer maxAdult;
        private Integer maxChild;
        private Integer numberOfBeds;
        private Integer quantity;
        private Long priceMin;
        private Long priceMax;
        private Integer checkInTime;
        private Integer checkOutTime;
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonDeserialize(using = LocalDateDeserializer.class)
        private LocalDate availableFrom;
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonDeserialize(using = LocalDateDeserializer.class)
        private LocalDate availableUntil;
        List<String> facilityDisplayNameList;

        public interface SearchConsumer {}
}
