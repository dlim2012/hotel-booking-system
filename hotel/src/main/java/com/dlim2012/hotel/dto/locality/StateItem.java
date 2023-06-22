package com.dlim2012.hotel.dto.locality;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StateItem {
    @NotNull(groups = {Put.class})
    private Integer id;
    @NotNull(groups = {Post.class})
    private String name;
    private String initials;
    private String areaCode;
    @NotNull(groups = {Post.class, Put.class})
    private Integer countryId;

    public interface Post {}

    public interface Put {}
}
