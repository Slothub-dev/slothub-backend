package com.slothub.space;

import com.slothub.space.dto.SpaceRequest;
import com.slothub.space.dto.SpaceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SpaceMapper {

    @Mapping(target = "venueId", source = "venue.id")
    SpaceResponse toResponse(Space space);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "venue", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "createdAt", ignore = true)
    Space toEntity(SpaceRequest request);
}
