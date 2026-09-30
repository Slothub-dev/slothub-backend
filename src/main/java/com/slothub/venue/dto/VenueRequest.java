package com.slothub.venue.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalTime;

public record VenueRequest(
        @NotBlank @Size(max = 255) String name,
        @NotBlank @Size(max = 100) String city,
        @NotBlank @Size(max = 500) String address,
        @NotBlank String timezone,
        @NotNull LocalTime opensAt,
        @NotNull LocalTime closesAt) {
}
