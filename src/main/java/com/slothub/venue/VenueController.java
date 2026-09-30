package com.slothub.venue;

import com.slothub.venue.dto.VenueRequest;
import com.slothub.venue.dto.VenueResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/venues")
@RequiredArgsConstructor
@Tag(name = "Venues", description = "Venues (clubs, business centers, coworkings)")
public class VenueController {

    private final VenueService venueService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a venue")
    public VenueResponse create(@Valid @RequestBody VenueRequest request) {
        return venueService.create(request);
    }

    @GetMapping
    @Operation(summary = "List venues, optionally filtered by city")
    public List<VenueResponse> findAll(@RequestParam(required = false) String city) {
        return venueService.findAll(city);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a venue by id")
    public VenueResponse getById(@PathVariable Long id) {
        return venueService.getById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a venue")
    public VenueResponse update(@PathVariable Long id, @Valid @RequestBody VenueRequest request) {
        return venueService.update(id, request);
    }
}
