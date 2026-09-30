package com.slothub.booking.dto;

import com.slothub.booking.BookingStatus;
import java.math.BigDecimal;
import java.time.Instant;

public record BookingResponse(
        Long id,
        Long spaceId,
        String spaceName,
        String venueName,
        String customerName,
        String customerEmail,
        Instant startsAt,
        Instant endsAt,
        BookingStatus status,
        BigDecimal totalPrice,
        Instant createdAt) {
}
