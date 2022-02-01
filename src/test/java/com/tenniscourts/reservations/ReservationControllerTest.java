package com.tenniscourts.reservations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenniscourts.TestConfig;
import junit.framework.TestCase;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReservationControllerTest extends TestConfig {

    @Test
    @Transactional
    public void bookReservation() throws Exception {
        CreateReservationRequestDTO createReservationRequestDTO = new CreateReservationRequestDTO();
        createReservationRequestDTO.setGuestId(1L);
        createReservationRequestDTO.setScheduleId(1L);

        mvc.perform(post("/reservations").contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(createReservationRequestDTO))).andExpect(status().isCreated());
    }

    @Test
    @Transactional
    public void bookReservationWrongGuestId() throws Exception {
        CreateReservationRequestDTO createReservationRequestDTO = new CreateReservationRequestDTO();
        createReservationRequestDTO.setGuestId(8L);
        createReservationRequestDTO.setScheduleId(1L);

        mvc.perform(post("/reservations").contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(createReservationRequestDTO))).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void bookReservationScheduleIdNotPresentInDB() throws Exception {
        CreateReservationRequestDTO createReservationRequestDTO = new CreateReservationRequestDTO();
        createReservationRequestDTO.setGuestId(1L);
        createReservationRequestDTO.setScheduleId(8L);

        mvc.perform(post("/reservations").contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(createReservationRequestDTO))).andExpect(status().isNotFound());
    }

    @Test
    public void retrievePastReservations() throws Exception {
        ReservationRequestFilterDTO reservationFilterRequestDTO = new ReservationRequestFilterDTO();
        reservationFilterRequestDTO.setStartDateTime(LocalDateTime.now().minusYears(50));
        reservationFilterRequestDTO.setEndDateTime(LocalDateTime.now());

        MvcResult result = mvc.perform(post("/reservations/filters").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(reservationFilterRequestDTO))).andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        List reservations = new ObjectMapper().readValue(json, List.class);

        assertEquals(0, reservations.size());
    }

    @Test
    public void retrieveReservationsBetween() throws Exception {
        ReservationRequestFilterDTO reservationFilterRequestDTO = new ReservationRequestFilterDTO();
        reservationFilterRequestDTO.setStartDateTime(LocalDateTime.now().minusYears(50));
        reservationFilterRequestDTO.setEndDateTime(LocalDateTime.now().plusYears(50));

        MvcResult result = mvc.perform(post("/reservations/filters").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(reservationFilterRequestDTO))).andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        List reservations = new ObjectMapper().readValue(json, List.class);

        assertEquals(3, reservations.size());
    }

    @Test
    @Transactional
    public void bookReservationScheduleSlotAlreadyTaken() throws Exception {
        CreateReservationRequestDTO createReservationRequestDTO = new CreateReservationRequestDTO();
        createReservationRequestDTO.setGuestId(1L);
        createReservationRequestDTO.setScheduleId(2L);

        mvc.perform(post("/reservations").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createReservationRequestDTO))).andExpect(status().isConflict());
    }

    @Test
    @Transactional
    public void bookReservationScheduleInThePast() throws Exception {
        CreateReservationRequestDTO createReservationRequestDTO = new CreateReservationRequestDTO();
        createReservationRequestDTO.setGuestId(1L);
        createReservationRequestDTO.setScheduleId(3L);

        mvc.perform(post("/reservations").contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createReservationRequestDTO))).andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void cancelReservation() throws Exception {
        MvcResult result = mvc.perform(delete("/reservations/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        ReservationDTO reservationDTO = new ObjectMapper().readValue(json, ReservationDTO.class);

        assertNotNull(reservationDTO);
        TestCase.assertEquals(1, (long) reservationDTO.getId());
    }

    @Test
    @Transactional
    public void cancelReservationWrongStatus() throws Exception {
        mvc.perform(delete("/reservations/2").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void rescheduleReservationOtherSlot() throws Exception {
        mvc.perform(put("/reservations/1/1").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void rescheduleReservationSameSlot() throws Exception {
        mvc.perform(put("/reservations/1/2").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }
}