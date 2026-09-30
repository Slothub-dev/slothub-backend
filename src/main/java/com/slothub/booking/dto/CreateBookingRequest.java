package com.slothub.booking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record CreateBookingRequest(
        @NotNull Long spaceId,
        @NotBlank String customerName,
        @NotBlank @Email String customerEmail,
        @NotNull Instant startsAt,
        @NotNull Instant endsAt) {
}
