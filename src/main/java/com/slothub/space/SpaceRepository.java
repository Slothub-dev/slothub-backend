package com.slothub.space;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpaceRepository extends JpaRepository<Space, Long> {

    List<Space> findAllByVenueId(Long venueId);
}
