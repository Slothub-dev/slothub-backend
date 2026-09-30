package com.slothub.booking;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.slothub.AbstractIntegrationTest;
import com.slothub.booking.dto.CreateBookingRequest;
import com.slothub.space.Space;
import com.slothub.space.SpaceRepository;
import com.slothub.space.SpaceType;
import com.slothub.venue.Venue;
import com.slothub.venue.VenueRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

class BookingControllerIT extends AbstractIntegrationTest {

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private SpaceRepository spaceRepository;

    private Space space;

    @BeforeEach
    void setUp() {
        Venue venue = new Venue();
        venue.setName("Padel Club Center");
        venue.setCity("Moscow");
        venue.setAddress("Lenina st. 1");
        venue.setTimezone("Europe/Moscow");
        venue.setOpensAt(LocalTime.of(8, 0));
        venue.setClosesAt(LocalTime.of(23, 0));
        venueRepository.save(venue);

        space = new Space();
        space.setVenue(venue);
        space.setName("Court 1");
        space.setType(SpaceType.COURT);
        space.setCapacity(4);
        space.setPricePerHour(new BigDecimal("2000.00"));
        space = spaceRepository.save(space);
    }

    @Test
    void createsBooking() throws Exception {
        CreateBookingRequest request = new CreateBookingRequest(space.getId(), "Ivan Petrov", "ivan@mail.ru",
                Instant.parse("2026-10-01T10:00:00Z"), Instant.parse("2026-10-01T12:00:00Z"));

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.totalPrice").value(4000.00))
                .andExpect(jsonPath("$.venueName").value("Padel Club Center"));
    }

    @Test
    void findsBookingsOfCustomer() throws Exception {
        createBooking("anna@mail.ru", "2026-10-01T10:00:00Z", "2026-10-01T11:00:00Z");
        createBooking("anna@mail.ru", "2026-10-02T10:00:00Z", "2026-10-02T11:00:00Z");
        createBooking("oleg@mail.ru", "2026-10-03T10:00:00Z", "2026-10-03T11:00:00Z");

        mockMvc.perform(get("/api/v1/bookings").param("customerEmail", "anna@mail.ru"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void returns404WhenBookingDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/bookings/{id}", 999_999))
                .andExpect(status().isNotFound());
    }

    private void createBooking(String email, String startsAt, String endsAt) throws Exception {
        CreateBookingRequest request = new CreateBookingRequest(space.getId(), "Customer", email,
                Instant.parse(startsAt), Instant.parse(endsAt));
        mockMvc.perform(post("/api/v1/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}
