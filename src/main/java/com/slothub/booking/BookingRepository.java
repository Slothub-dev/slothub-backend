package com.slothub.booking;

import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
            select count(b) > 0
            from Booking b
            where b.space.id = :spaceId
              and b.status = com.slothub.booking.BookingStatus.CONFIRMED
              and b.startsAt < :endsAt
              and b.endsAt > :startsAt
            """)
    boolean existsOverlapping(Long spaceId, Instant startsAt, Instant endsAt);

    List<Booking> findAllBySpaceIdAndStartsAtBetween(Long spaceId, Instant from, Instant to);

    List<Booking> findAllByCustomerEmailOrderByStartsAtDesc(String customerEmail);
}
