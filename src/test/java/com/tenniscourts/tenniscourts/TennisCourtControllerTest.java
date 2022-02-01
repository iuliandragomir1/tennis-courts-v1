package com.tenniscourts.tenniscourts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenniscourts.TestConfig;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static junit.framework.TestCase.assertNotNull;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TennisCourtControllerTest extends TestConfig {

    @Test
    public void findTennisCourtById() throws Exception {
        MvcResult result = mvc.perform(get("/tennis-courts/1")).andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        TennisCourtDTO tennisCourt = new ObjectMapper().readValue(json, TennisCourtDTO.class);

        assertNotNull(tennisCourt);
    }

    @Test
    public void findTennisCourtWithSchedulesById() throws Exception {
        MvcResult result = mvc.perform(get("/tennis-courts/1/schedules")).andExpect(status().isOk()).andReturn();

        String json = result.getResponse().getContentAsString();
        TennisCourtDTO tennisCourt = new ObjectMapper().readValue(json, TennisCourtDTO.class);

        assertNotNull(tennisCourt);
    }

    @Test
    public void findTennisCourtByIdNotFound() throws Exception {
        mvc.perform(get("/tennis-courts/10")).andExpect(status().isNotFound());
    }

    @Test
    public void findTennisCourtWithSchedulesByIdNotFound() throws Exception {
        mvc.perform(get("/tennis-courts/10/schedules")).andExpect(status().isNotFound());
    }

    @Test
    public void addTennisCourtTestBadRequest() throws Exception {
        mvc.perform(post("/tennis-courts").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addTennisCourtTest() throws Exception {
        TennisCourtDTO tennisCourtDTO = TennisCourtDTO.builder().name("Roland Garros").build();

        mvc.perform(post("/tennis-courts", tennisCourtDTO).contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(tennisCourtDTO)))
                .andExpect(status().isCreated());
    }

}
