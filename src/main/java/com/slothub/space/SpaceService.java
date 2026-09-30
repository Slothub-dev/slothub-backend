package com.slothub.space;

import com.slothub.common.exception.NotFoundException;
import com.slothub.space.dto.SpaceRequest;
import com.slothub.space.dto.SpaceResponse;
import com.slothub.venue.Venue;
import com.slothub.venue.VenueService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SpaceService {

    private final SpaceRepository spaceRepository;
    private final SpaceMapper spaceMapper;
    private final VenueService venueService;

    @Transactional
    public SpaceResponse create(Long venueId, SpaceRequest request) {
        Venue venue = venueService.findVenue(venueId);
        Space space = spaceMapper.toEntity(request);
        space.setVenue(venue);
        return spaceMapper.toResponse(spaceRepository.save(space));
    }

    @Transactional(readOnly = true)
    public List<SpaceResponse> findAllByVenue(Long venueId) {
        return spaceRepository.findAllByVenueId(venueId).stream()
                .map(spaceMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SpaceResponse getById(Long id) {
        return spaceMapper.toResponse(findSpace(id));
    }

    /**
     * Returns the entity itself. Intended for other modules that need a reference to a space.
     */
    @Transactional(readOnly = true)
    public Space findSpace(Long id) {
        return spaceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Space with id " + id + " not found"));
    }
}
