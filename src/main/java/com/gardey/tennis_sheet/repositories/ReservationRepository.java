package com.gardey.tennis_sheet.repositories;

import com.gardey.tennis_sheet.models.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("select case when count(r) > 0 then true else false end from Reservation r where r.court.id = :courtId and r.start < :end and r.end > :start")
    boolean existsByCourtIdAndTimeRangeOverlap(@Param("courtId") Long courtId, @Param("start") Instant start, @Param("end") Instant end);

    List<Reservation> findByCourtIdAndStartBetween(Long courtId, Instant start, Instant end);
}
