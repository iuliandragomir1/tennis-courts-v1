package com.tenniscourts.schedules;

import com.tenniscourts.config.BaseRestController;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("schedules")
public class ScheduleController extends BaseRestController {

    private final ScheduleService scheduleService;

    @PostMapping
    @ApiOperation(value = "Add a new schedule slot for a tennis court")
    public ResponseEntity<Void> addScheduleTennisCourt(@RequestBody CreateScheduleRequestDTO createScheduleRequestDTO) {
        return ResponseEntity.created(locationByEntity(scheduleService.addSchedule(createScheduleRequestDTO.getTennisCourtId(), createScheduleRequestDTO).getId())).build();
    }

    @PostMapping("/filters")
    @ApiOperation(value = "Find all the schedules between two given dates")
    public ResponseEntity<List<ScheduleDTO>> findSchedulesByDates(@RequestBody ScheduleFilterRequestDTO scheduleFilterRequestDTO) {
        return ResponseEntity.ok(scheduleService.findSchedulesByDates(scheduleFilterRequestDTO.getStartDateTime(), scheduleFilterRequestDTO.getEndDateTime()));
    }

    @GetMapping("/{scheduleId}")
    @ApiOperation(value = "Find a schedule")
    public ResponseEntity<ScheduleDTO> findByScheduleId(@PathVariable Long scheduleId) {
        return ResponseEntity.ok(scheduleService.findSchedule(scheduleId));
    }

    @PostMapping("/filters/available")
    @ApiOperation(value = "Find available schedules")
    public ResponseEntity<List<ScheduleDTO>> findAvailableSchedules(@RequestBody ScheduleFilterRequestDTO scheduleFilterRequestDTO) {
        return ResponseEntity.ok(scheduleService.findAvailableSchedules(scheduleFilterRequestDTO.getStartDateTime(),
                scheduleFilterRequestDTO.getEndDateTime()));
    }
}
