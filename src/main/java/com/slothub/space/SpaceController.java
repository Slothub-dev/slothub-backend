package com.slothub.space;

import com.slothub.space.dto.SpaceRequest;
import com.slothub.space.dto.SpaceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Spaces", description = "Bookable spaces inside a venue")
public class SpaceController {

    private final SpaceService spaceService;

    @PostMapping("/venues/{venueId}/spaces")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a space to a venue")
    public SpaceResponse create(@PathVariable Long venueId, @Valid @RequestBody SpaceRequest request) {
        return spaceService.create(venueId, request);
    }

    @GetMapping("/venues/{venueId}/spaces")
    @Operation(summary = "List spaces of a venue")
    public List<SpaceResponse> findAllByVenue(@PathVariable Long venueId) {
        return spaceService.findAllByVenue(venueId);
    }

    @GetMapping("/spaces/{id}")
    @Operation(summary = "Get a space by id")
    public SpaceResponse getById(@PathVariable Long id) {
        return spaceService.getById(id);
    }
}
