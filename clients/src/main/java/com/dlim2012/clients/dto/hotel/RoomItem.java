package com.dlim2012.clients.dto.hotel;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomItem {
        @NotNull(groups = {SearchConsumer.class})
        private Integer id;
        @Null(groups = {Post.class, Put.class})
        private Integer hotelId;
        @NotNull(groups = {Post.class, Put.class})
        private String displayName;
        @NotNull(groups = {Post.class, Put.class})
        private String description;
        @NotNull(groups = {Post.class, Put.class})
        private Boolean isActive;
        @Positive(groups = {Post.class, Put.class})
        private Integer maxAdult;
        @PositiveOrZero(groups = {Post.class, Put.class})
        private Integer maxChild;
        @PositiveOrZero(groups = {Post.class, Put.class})
        private Integer quantity;
        @Positive(groups = {Post.class, Put.class})
        private Double priceMin;
        @Positive(groups = {Post.class, Put.class})
        private Double priceMax;
        private Integer checkInTime;
        private Integer checkOutTime;
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonDeserialize(using = LocalDateDeserializer.class)
        private LocalDate availableFrom;
        @JsonSerialize(using = LocalDateSerializer.class)
        @JsonDeserialize(using = LocalDateDeserializer.class)
        private LocalDate availableUntil;
        @Null(groups = {Post.class, Put.class})
        private String managerEmail;

        public interface Post {}

        public interface Put {}

        public interface SearchConsumer {}

        public RoomItem(Integer id, Integer quantity) {
                this.id = id;
                this.quantity = quantity;
        }

        public static RoomItem zeroQuantity(Integer newId) {
                return new RoomItem(newId, 0);
        }

}
