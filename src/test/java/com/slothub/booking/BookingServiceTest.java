package com.slothub.booking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.slothub.booking.dto.BookingResponse;
import com.slothub.booking.dto.CreateBookingRequest;
import com.slothub.space.Space;
import com.slothub.space.SpaceService;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    private static final Instant START = Instant.parse("2026-10-01T10:00:00Z");
    private static final Instant END = Instant.parse("2026-10-01T12:00:00Z");

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private SpaceService spaceService;

    @InjectMocks
    private BookingService bookingService;

    private Space space;

    @BeforeEach
    void setUp() {
        space = new Space();
        space.setId(1L);
        space.setName("Court 1");
        space.setPricePerHour(new BigDecimal("1500.00"));
        space.setActive(true);
    }

    @Test
    void createsBookingWhenSlotIsFree() {
        CreateBookingRequest request = new CreateBookingRequest(1L, "Ivan", "ivan@mail.ru", START, END);
        when(spaceService.findSpace(1L)).thenReturn(space);
        when(bookingRepository.existsOverlapping(1L, START, END)).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));
        when(bookingMapper.toResponse(any(Booking.class))).thenReturn(stubResponse());

        BookingResponse response = bookingService.create(request);

        assertThat(response).isNotNull();
        ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(captor.capture());
        Booking saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
        assertThat(saved.getTotalPrice()).isEqualByComparingTo("3000.00");
    }

    @ParameterizedTest(name = "Cost per Hour {0}, Minutes {1}, Total Cost {2}")
    @CsvSource({
            "2000.00, 90, 3000.00",
            "1000.00, 70, 1166.67",
            "1000.00, 10, 166.67",
            "1000.00, 11, 183.33"
    })
    void calculatesPriceProportionallyToDuration(BigDecimal costPerHour, int minutes, BigDecimal totalCost) {
        Instant end = START.plus(minutes, ChronoUnit.MINUTES);
        space.setPricePerHour(costPerHour);

        CreateBookingRequest request = new CreateBookingRequest(1L, "Ivan", "ivan@mail.ru", START, end);
        when(spaceService.findSpace(1L)).thenReturn(space);
        when(bookingRepository.existsOverlapping(1L, START, end)).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        bookingService.create(request);

        ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(captor.capture());
        Booking saved = captor.getValue();
        assertThat(saved.getTotalPrice()).isEqualByComparingTo(totalCost);
    }

    @Test
    void throwsWhenSlotIsAlreadyBooked() {
        CreateBookingRequest request = new CreateBookingRequest(1L, "Ivan", "ivan@mail.ru", START, END);
        when(spaceService.findSpace(1L)).thenReturn(space);
        when(bookingRepository.existsOverlapping(1L, START, END)).thenReturn(true);

        assertThatThrownBy(() -> bookingService.create(request))
                .isInstanceOf(IllegalStateException.class);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void throwsWhenSpaceIsInactive() {
        space.setActive(false);
        CreateBookingRequest request = new CreateBookingRequest(1L, "Ivan", "ivan@mail.ru", START, END);
        when(spaceService.findSpace(1L)).thenReturn(space);

        assertThatThrownBy(() -> bookingService.create(request))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void throwsWhenEndIsBeforeStart() {
        CreateBookingRequest request = new CreateBookingRequest(1L, "Ivan", "ivan@mail.ru", END, START);

        assertThatThrownBy(() -> bookingService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private static BookingResponse stubResponse() {
        return new BookingResponse(1L, 1L, "Court 1", "Club", "Ivan", "ivan@mail.ru",
                START, END, BookingStatus.CONFIRMED, new BigDecimal("3000.00"), Instant.now());
    }
}
