package com.tenniscourts.schedules;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenniscourts.TestConfig;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ScheduleControllerTest extends TestConfig {

    @Test
    public void findSchedulesByDates() throws Exception {
        LocalDateTime startDateTime = LocalDateTime.now().minusYears(50);
        LocalDateTime endDateTime = LocalDateTime.now().plusYears(10);
        ScheduleFilterRequestDTO scheduleFilterRequestDTO = new ScheduleFilterRequestDTO();
        scheduleFilterRequestDTO.setStartDateTime(startDateTime);
        scheduleFilterRequestDTO.setEndDateTime(endDateTime);

        MvcResult result = mvc.perform(post("/schedules/filters").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(scheduleFilterRequestDTO))).andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        List availableTimeslots = new ObjectMapper().readValue(json, List.class);

        assertEquals(3, availableTimeslots.size());
    }

    @Test
    public void findAvailableSchedules() throws Exception {
        LocalDateTime startDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = startDateTime.plusYears(10);
        ScheduleFilterRequestDTO scheduleFilterRequestDTO = new ScheduleFilterRequestDTO();
        scheduleFilterRequestDTO.setStartDateTime(startDateTime);
        scheduleFilterRequestDTO.setEndDateTime(endDateTime);

        MvcResult result = mvc.perform(post("/schedules/filters/available").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(scheduleFilterRequestDTO))).andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        List availableTimeslots = new ObjectMapper().readValue(json, List.class);

        assertEquals(2, availableTimeslots.size());
    }

    @Test
    @Transactional
    public void addScheduleTennisCourt() throws Exception {
        CreateScheduleRequestDTO createScheduleRequestDTO = new CreateScheduleRequestDTO();
        createScheduleRequestDTO.setStartDateTime(LocalDateTime.now().plusYears(1L));
        createScheduleRequestDTO.setTennisCourtId(1L);

        mvc.perform(post("/schedules").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createScheduleRequestDTO))).andExpect(status().isCreated());
    }

    @Test
    @Transactional
    public void addScheduleTennisCourtPastDate() throws Exception {
        CreateScheduleRequestDTO createScheduleRequestDTO = new CreateScheduleRequestDTO();
        createScheduleRequestDTO.setStartDateTime(LocalDateTime.now());
        createScheduleRequestDTO.setTennisCourtId(1L);

        mvc.perform(post("/schedules").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createScheduleRequestDTO))).andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void addScheduleTennisCourtAlreadyExists() throws Exception {
        CreateScheduleRequestDTO createScheduleRequestDTO = new CreateScheduleRequestDTO();
        createScheduleRequestDTO.setStartDateTime(LocalDateTime.parse("2023-01-01T12:00:00.0"));
        createScheduleRequestDTO.setTennisCourtId(1L);

        mvc.perform(post("/schedules").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createScheduleRequestDTO))).andExpect(status().isConflict());
    }

}