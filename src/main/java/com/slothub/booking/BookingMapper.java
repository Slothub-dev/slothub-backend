package com.slothub.booking;

import com.slothub.booking.dto.BookingResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "spaceId", source = "space.id")
    @Mapping(target = "spaceName", source = "space.name")
    @Mapping(target = "venueName", source = "space.venue.name")
    BookingResponse toResponse(Booking booking);
}
