package com.dlim2012.clients.dto.hotel.facility;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FacilityItem {
    @NotNull(groups = {FacilityItem.Put.class})
    private Integer id;
    @NotNull(groups = {FacilityItem.Post.class})
    private String displayName;

    public interface Post {}

    public interface Put {}

    public static FacilityItem onlyId(Integer id){
        return FacilityItem.builder()
                .id(id)
                .displayName(null)
                .build();
    }
}
