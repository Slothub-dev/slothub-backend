package com.slothub.booking;

import com.slothub.booking.dto.BookingResponse;
import com.slothub.booking.dto.CreateBookingRequest;
import com.slothub.common.exception.NotFoundException;
import com.slothub.space.Space;
import com.slothub.space.SpaceService;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final SpaceService spaceService;

    @Transactional
    public BookingResponse create(CreateBookingRequest request) {
        if (!request.endsAt().isAfter(request.startsAt())) {
            throw new IllegalArgumentException("endsAt must be after startsAt");
        }

        Space space = spaceService.findSpace(request.spaceId());
        if (!space.isActive()) {
            throw new IllegalStateException("Space " + space.getId() + " is not available for booking");
        }

        if (bookingRepository.existsOverlapping(space.getId(), request.startsAt(), request.endsAt())) {
            throw new IllegalStateException("Space is already booked for this time");
        }

        Booking booking = new Booking();
        booking.setSpace(space);
        booking.setCustomerName(request.customerName());
        booking.setCustomerEmail(request.customerEmail());
        booking.setStartsAt(request.startsAt());
        booking.setEndsAt(request.endsAt());
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setTotalPrice(calculatePrice(space, request.startsAt(), request.endsAt()));

        Booking saved = bookingRepository.save(booking);
        log.info("Booking {} created for space {}", saved.getId(), space.getId());
        return bookingMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public BookingResponse getById(Long id) {
        return bookingRepository.findById(id)
                .map(bookingMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Booking with id " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> findBySpaceAndDate(Long spaceId, LocalDate date) {
        Instant from = date.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant to = date.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        return bookingRepository.findAllBySpaceIdAndStartsAtBetween(spaceId, from, to).stream()
                .map(bookingMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> findByCustomer(String customerEmail) {
        return bookingRepository.findAllByCustomerEmailOrderByStartsAtDesc(customerEmail).stream()
                .map(bookingMapper::toResponse)
                .toList();
    }

    private BigDecimal calculatePrice(Space space, Instant startsAt, Instant endsAt) {
        long hours = Duration.between(startsAt, endsAt).toHours();
        return space.getPricePerHour().multiply(BigDecimal.valueOf(hours));
    }
}
