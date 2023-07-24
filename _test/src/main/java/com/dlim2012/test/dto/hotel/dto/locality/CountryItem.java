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
public class CountryItem {
        @NotNull(groups = {Put.class})
        private Integer id;
        @NotNull(groups = {Post.class})
        private String name;
        @NotNull(groups = {Post.class})
        private String initials;

        public interface Post {}
        public interface Put {}
}
