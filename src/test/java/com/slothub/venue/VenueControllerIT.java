package com.slothub.venue;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.slothub.AbstractIntegrationTest;
import com.slothub.venue.dto.VenueRequest;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

class VenueControllerIT extends AbstractIntegrationTest {

    @Autowired
    private VenueRepository venueRepository;

    @Test
    void createsVenue() throws Exception {
        VenueRequest request = venueRequest("Padel Club Center", "Moscow");

        mockMvc.perform(post("/api/v1/venues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name").value("Padel Club Center"))
                .andExpect(jsonPath("$.timezone").value("Europe/Moscow"));
    }

    @Test
    void rejectsVenueWithoutName() throws Exception {
        VenueRequest request = venueRequest("", "Moscow");

        mockMvc.perform(post("/api/v1/venues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returnsVenueById() throws Exception {
        Venue venue = venueRepository.save(venue("Tennis Park", "Kazan"));

        mockMvc.perform(get("/api/v1/venues/{id}", venue.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Tennis Park"));
    }

    @Test
    void returns404WhenVenueDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/venues/{id}", 999_999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Venue with id 999999 not found"));
    }

    @Test
    void filtersVenuesByCity() throws Exception {
        venueRepository.save(venue("Kazan Arena", "Kazan"));
        venueRepository.save(venue("Sochi Courts", "Sochi"));

        mockMvc.perform(get("/api/v1/venues").param("city", "sochi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Sochi Courts"));
    }

    @Test
    void updatesVenue() throws Exception {
        Venue venue = venueRepository.save(venue("Old Name", "Moscow"));
        VenueRequest request = venueRequest("New Name", "Moscow");

        mockMvc.perform(put("/api/v1/venues/{id}", venue.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"));
    }

    private static VenueRequest venueRequest(String name, String city) {
        return new VenueRequest(name, city, "Lenina st. 1", "Europe/Moscow",
                LocalTime.of(8, 0), LocalTime.of(23, 0));
    }

    private static Venue venue(String name, String city) {
        Venue venue = new Venue();
        venue.setName(name);
        venue.setCity(city);
        venue.setAddress("Lenina st. 1");
        venue.setTimezone("Europe/Moscow");
        venue.setOpensAt(LocalTime.of(8, 0));
        venue.setClosesAt(LocalTime.of(23, 0));
        return venue;
    }
}
