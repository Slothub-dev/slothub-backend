package com.slothub.venue.dto;

import java.time.Instant;
import java.time.LocalTime;

public record VenueResponse(
        Long id,
        String name,
        String city,
        String address,
        String timezone,
        LocalTime opensAt,
        LocalTime closesAt,
        Instant createdAt) {
}
