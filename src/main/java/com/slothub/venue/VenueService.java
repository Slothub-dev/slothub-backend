package com.slothub.venue;

import com.slothub.common.exception.NotFoundException;
import com.slothub.venue.dto.VenueRequest;
import com.slothub.venue.dto.VenueResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VenueService {

    private final VenueRepository venueRepository;
    private final VenueMapper venueMapper;

    @Transactional
    public VenueResponse create(VenueRequest request) {
        Venue venue = venueRepository.save(venueMapper.toEntity(request));
        return venueMapper.toResponse(venue);
    }

    @Transactional(readOnly = true)
    public VenueResponse getById(Long id) {
        return venueMapper.toResponse(findVenue(id));
    }

    @Transactional(readOnly = true)
    public List<VenueResponse> findAll(String city) {
        List<Venue> venues = city == null
                ? venueRepository.findAll()
                : venueRepository.findAllByCityIgnoreCase(city);
        return venues.stream()
                .map(venueMapper::toResponse)
                .toList();
    }

    @Transactional
    public VenueResponse update(Long id, VenueRequest request) {
        Venue venue = findVenue(id);
        venueMapper.update(request, venue);
        return venueMapper.toResponse(venue);
    }

    /**
     * Returns the entity itself. Intended for other modules that need a reference to a venue.
     */
    @Transactional(readOnly = true)
    public Venue findVenue(Long id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Venue with id " + id + " not found"));
    }
}
