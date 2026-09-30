package com.slothub.space.dto;

import com.slothub.space.SpaceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record SpaceRequest(
        @NotBlank @Size(max = 255) String name,
        @NotNull SpaceType type,
        @NotNull @Positive Integer capacity,
        @NotNull @PositiveOrZero BigDecimal pricePerHour) {
}
