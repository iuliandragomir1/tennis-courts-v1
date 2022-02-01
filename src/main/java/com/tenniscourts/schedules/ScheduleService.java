package com.tenniscourts.schedules;

import com.tenniscourts.exceptions.AlreadyExistsEntityException;
import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.reservations.ReservationStatus;
import com.tenniscourts.tenniscourts.TennisCourt;
import com.tenniscourts.tenniscourts.TennisCourtDTO;
import com.tenniscourts.tenniscourts.TennisCourtMapper;
import com.tenniscourts.tenniscourts.TennisCourtRepository;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    @Getter
    private final ScheduleMapper scheduleMapper;

    private final TennisCourtMapper tennisCourtMapper;

    private final TennisCourtRepository tennisCourtRepository;

    public ScheduleDTO addSchedule(Long tennisCourtId, CreateScheduleRequestDTO createScheduleRequestDTO) {
        if (createScheduleRequestDTO.getStartDateTime() == null) {
            throw new IllegalArgumentException("Start date and time not present");
        }
        if (createScheduleRequestDTO.getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot add schedules for past dates");
        }

        checkScheduleNotAlreadyExists(tennisCourtId, createScheduleRequestDTO);
        Schedule schedule = buildNewScheduleSlot(tennisCourtId, createScheduleRequestDTO);

        return scheduleMapper.map(scheduleRepository.save(schedule));
    }

    public List<ScheduleDTO> findSchedulesByDates(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<Schedule> schedules =
            new ArrayList<>(scheduleRepository
                .findAllByStartDateTimeGreaterThanEqualAndEndDateTimeLessThanEqual(startDateTime, endDateTime));

        return scheduleMapper.map(schedules);
    }

    public ScheduleDTO findSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId).map(scheduleMapper::map).<EntityNotFoundException>orElseThrow(() -> {
            throw new EntityNotFoundException("Schedule with id = " + scheduleId + " was not found");
        });
    }

    public List<ScheduleDTO> findSchedulesByTennisCourtId(Long tennisCourtId) {
        return scheduleMapper.map(scheduleRepository.findByTennisCourt_IdOrderByStartDateTime(tennisCourtId));
    }

    public List<ScheduleDTO> findAvailableSchedules(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<Schedule> availableScheduleSlots =
                scheduleRepository.findAllByStartDateTimeGreaterThanEqualAndEndDateTimeLessThanEqual(startDateTime, endDateTime)
                        .stream().filter(availableSchedulesFilter()).collect(Collectors.toList());

        return scheduleMapper.map(availableScheduleSlots);
    }

    private Predicate<Schedule> availableSchedulesFilter() {
        return schedule -> {
            boolean scheduleAlreadyReserved = schedule.getReservations().stream()
                    .anyMatch(reservation -> ReservationStatus.READY_TO_PLAY.equals(reservation.getReservationStatus()));

            return !scheduleAlreadyReserved;
        };
    }

    private void checkScheduleNotAlreadyExists(Long tennisCourtId, CreateScheduleRequestDTO createScheduleRequestDTO) {
        if (scheduleRepository.findByTennisCourt_IdAndStartDateTime(
                tennisCourtId, createScheduleRequestDTO.getStartDateTime()).isPresent()) {
            throw new AlreadyExistsEntityException("There is already a schedule slot created for the given interval");
        }

    }

    private Schedule buildNewScheduleSlot(Long tennisCourtId, CreateScheduleRequestDTO createScheduleRequestDTO) {
        TennisCourtDTO tennisCourtDTO = tennisCourtRepository.findById(tennisCourtId).map(tennisCourtMapper::map).<EntityNotFoundException>orElseThrow(() -> {
            throw new EntityNotFoundException("Tennis Court not found");
        });
        TennisCourt tennisCourt = tennisCourtMapper.map(tennisCourtDTO);
        LocalDateTime scheduleStart = createScheduleRequestDTO.getStartDateTime();
        LocalDateTime scheduleEnd = scheduleStart.plusHours(1L);

        return Schedule.builder()
            .tennisCourt(tennisCourt)
            .startDateTime(scheduleStart)
            .endDateTime(scheduleEnd)
            .build();
    }
}
