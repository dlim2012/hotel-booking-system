package com.dlim2012.test.dto.hotel.dto.locality;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocalityItem {
        @NotNull(groups = {Put.class})
        private Integer id;
        @NotNull(groups = {StateItem.Post.class})
        private String zipcode;
        @NotNull(groups = {StateItem.Post.class})
        private Integer cityId;

        public interface Post {}

        public interface Put {}
}
