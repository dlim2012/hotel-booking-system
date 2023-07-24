package com.dlim2012.clients.dto.booking;

import com.dlim2012.clients.entity.BookingStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingItem{
        @Null(groups = {Post.class})
        private Long id;

        @Null(groups = {Post.class})
        private Integer userId;

        @Null(groups = {Post.class})
        private Integer hotelId;

        @Null(groups = {Post.class})
        private Integer roomId;

        @NotNull
        private LocalDateTime startDateTime;

        @NotNull
        private LocalDateTime endDateTime;

        @NotNull
        private Integer quantity;

        @Null(groups = {Post.class})
        private BookingStatus status;

        @NotNull
        private Long priceInCents;

        @NotNull
        private LocalDateTime confirmTime;


        public interface Post {}
}
