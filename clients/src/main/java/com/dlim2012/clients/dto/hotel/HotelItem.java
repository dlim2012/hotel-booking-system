package com.dlim2012.clients.dto.hotel;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelItem {
        @NotNull(groups = {SearchConsumer.class})
        @Null(groups = {RoomItem.Post.class, RoomItem.Put.class})
        private Integer id;
        @NotNull(groups = {Post.class, Put.class})
        private String displayName;
        @NotNull(groups = {Put.class})
        private String description;
        @NotNull(groups = {Post.class, Put.class})
        private Boolean isActive;
        @NotNull(groups = {Post.class, Put.class})
        private String addressLine1;
        @NotNull(groups = {Put.class})
        private String addressLine2;
        @NotNull(groups = {Post.class, Put.class})
        private String zipcode;
        @NotNull(groups = {Post.class, Put.class})
        private String city;
        @NotNull(groups = {Post.class, Put.class})
        private String state;
        @NotNull(groups = {Post.class, Put.class})
        private String country;
        @Null(groups = {RoomItem.Post.class, RoomItem.Put.class})
        private String managerEmail;

        public interface Post {}

        public interface Put {}

        public interface SearchConsumer {}

        public HotelItem(Integer id) {
                this.id = id;
        }

}
