package com.dlim2012.clients.dto;

import jakarta.validation.constraints.NotNull;

public record IdItem(
        @NotNull
        Integer id

){
}
