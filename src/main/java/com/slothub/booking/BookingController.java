package com.slothub.booking;

import com.slothub.booking.dto.BookingResponse;
import com.slothub.booking.dto.CreateBookingRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Book a space")
    public BookingResponse create(@Valid @RequestBody CreateBookingRequest request) {
        return bookingService.create(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a booking by id")
    public BookingResponse getById(@PathVariable Long id) {
        return bookingService.getById(id);
    }

    @GetMapping(params = "spaceId")
    @Operation(summary = "Bookings of a space for a given date")
    public List<BookingResponse> findBySpaceAndDate(
            @RequestParam Long spaceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return bookingService.findBySpaceAndDate(spaceId, date);
    }

    @GetMapping(params = "customerEmail")
    @Operation(summary = "All bookings of a customer")
    public List<BookingResponse> findByCustomer(@RequestParam String customerEmail) {
        return bookingService.findByCustomer(customerEmail);
    }
}
