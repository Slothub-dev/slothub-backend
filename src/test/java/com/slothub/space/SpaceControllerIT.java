package com.slothub.space;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.slothub.AbstractIntegrationTest;
import com.slothub.space.dto.SpaceRequest;
import com.slothub.venue.Venue;
import com.slothub.venue.VenueRepository;
import java.math.BigDecimal;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

class SpaceControllerIT extends AbstractIntegrationTest {

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private SpaceRepository spaceRepository;

    private Venue venue;

    @BeforeEach
    void setUp() {
        venue = new Venue();
        venue.setName("Padel Club Center");
        venue.setCity("Moscow");
        venue.setAddress("Lenina st. 1");
        venue.setTimezone("Europe/Moscow");
        venue.setOpensAt(LocalTime.of(8, 0));
        venue.setClosesAt(LocalTime.of(23, 0));
        venue = venueRepository.save(venue);
    }

    @Test
    void createsSpaceInVenue() throws Exception {
        SpaceRequest request = new SpaceRequest("Court 1", SpaceType.COURT, 4, new BigDecimal("2500.00"));

        mockMvc.perform(post("/api/v1/venues/{venueId}/spaces", venue.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.venueId").value(venue.getId()))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void returns404WhenVenueDoesNotExist() throws Exception {
        SpaceRequest request = new SpaceRequest("Court 1", SpaceType.COURT, 4, new BigDecimal("2500.00"));

        mockMvc.perform(post("/api/v1/venues/{venueId}/spaces", 999_999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void listsSpacesOfVenue() throws Exception {
        spaceRepository.save(space("Court 1"));
        spaceRepository.save(space("Court 2"));

        mockMvc.perform(get("/api/v1/venues/{venueId}/spaces", venue.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    private Space space(String name) {
        Space space = new Space();
        space.setVenue(venue);
        space.setName(name);
        space.setType(SpaceType.COURT);
        space.setCapacity(4);
        space.setPricePerHour(new BigDecimal("2500.00"));
        return space;
    }
}
