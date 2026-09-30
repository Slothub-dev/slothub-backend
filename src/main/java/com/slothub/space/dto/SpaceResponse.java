package com.slothub.space.dto;

import com.slothub.space.SpaceType;
import java.math.BigDecimal;

public record SpaceResponse(
        Long id,
        Long venueId,
        String name,
        SpaceType type,
        Integer capacity,
        BigDecimal pricePerHour,
        boolean active) {
}
