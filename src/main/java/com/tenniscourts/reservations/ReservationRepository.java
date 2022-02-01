package com.tenniscourts.reservations;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findBySchedule_Id(Long scheduleId);

    List<Reservation> findBySchedule_StartDateTimeGreaterThanEqualAndSchedule_EndDateTimeLessThanEqual(LocalDateTime startDateTime, LocalDateTime endDateTime);

}
