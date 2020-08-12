package com.example.lotterymgmtapi.controller;

import com.example.lotterymgmtapi.entity.LotteryTicketRequest;
import com.example.lotterymgmtapi.entity.LotteryTicketResponse;
import com.example.lotterymgmtapi.model.Line;
import com.example.lotterymgmtapi.model.LotteryTicket;
import com.example.lotterymgmtapi.service.LotteryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LotteryController.class)
public class LotteryControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LotteryService lotteryService;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void testGetAllTicketsValidScenario() throws Exception {
        String id = "id1";
        String userId = "user1";
        boolean statusEnquired = false;

        LotteryTicketResponse response = new LotteryTicketResponse();
        response.setCreatedDateTime(new Date());
        response.setUpdatedDateTime(new Date());
        response.setId(id);
        response.setLines(List.of("000", "012"));
        response.setStatusEnquired(statusEnquired);
        response.setUserId(userId);

        List<LotteryTicketResponse> responseList = new ArrayList<>(List.of(response));
        when(lotteryService.getAllTickets()).thenReturn(responseList);
        this.mockMvc.perform(get("/lotteryapi/v1/ticket")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString(userId)));
    }

    @Test
    public void testGetAllTicketsEmptyResult() throws Exception {
        when(lotteryService.getAllTickets()).thenReturn(new ArrayList<>());
        this.mockMvc.perform(get("/lotteryapi/v1/ticket"))
                .andDo(print()).andExpect(status().isNoContent());
    }

    @Test
    public void testGetTicketByIdValidScenario() throws Exception {
        String id = "id1";
        String userId = "user1";
        boolean statusEnquired = false;

        LotteryTicketResponse response = new LotteryTicketResponse();
        response.setCreatedDateTime(new Date());
        response.setUpdatedDateTime(new Date());
        response.setId(id);
        response.setLines(List.of("000", "012"));
        response.setStatusEnquired(statusEnquired);
        response.setUserId(userId);

        when(lotteryService.getTicketById(id)).thenReturn(response);
        this.mockMvc.perform(get("/lotteryapi/v1/ticket/" + id))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString(id)));
    }

    @Test
    public void testGetTicketByIdInvalidId() throws Exception {
        when(lotteryService.getTicketById(anyString())).thenThrow(new NoSuchElementException());
        this.mockMvc.perform(get("/lotteryapi/v1/ticket/abc"))
                .andDo(print()).andExpect(status().isNotFound());
    }

    @Test
    public void createTicketValidScenario() throws Exception {
        String id = "id1";
        String userId = "user1";
        boolean statusEnquired = false;

        LotteryTicketRequest request = new LotteryTicketRequest();
        request.setLines(List.of("000", "012"));
        request.setUserId(userId);

        LotteryTicketResponse response = new LotteryTicketResponse();
        response.setCreatedDateTime(new Date());
        response.setUpdatedDateTime(new Date());
        response.setId(id);
        response.setLines(List.of("000", "012"));
        response.setStatusEnquired(statusEnquired);
        response.setUserId(userId);

        when(lotteryService.saveTicket(any(LotteryTicketRequest.class))).thenReturn(response);
        this.mockMvc.perform(post("/lotteryapi/v1/ticket/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString(id)));
    }

    @Test
    public void createTicketInvalidUserId() throws Exception {
        String id = "id1";
        String userId = "";
        boolean statusEnquired = false;

        LotteryTicketRequest request = new LotteryTicketRequest();
        request.setLines(List.of("000", "012", "001", "002"));
        request.setUserId(userId);

        this.mockMvc.perform(post("/lotteryapi/v1/ticket/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createTicketInvalidTicketLines() throws Exception {
        String id = "id1";
        String userId = "user1";
        boolean statusEnquired = false;

        LotteryTicketRequest request = new LotteryTicketRequest();
        request.setUserId(userId);

        this.mockMvc.perform(post("/lotteryapi/v1/ticket/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        request.setLines(List.of("0000", "012222"));

        this.mockMvc.perform(post("/lotteryapi/v1/ticket/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateTicketValidScenario() throws Exception {
        String id = "id1";
        String userId = "user1";
        boolean statusEnquired = false;

        LotteryTicketRequest request = new LotteryTicketRequest();
        request.setLines(List.of("000", "012"));
        request.setUserId(userId);

        LotteryTicketResponse response = new LotteryTicketResponse();
        response.setCreatedDateTime(new Date());
        response.setUpdatedDateTime(new Date());
        response.setId(id);
        response.setLines(List.of("000", "012"));
        response.setStatusEnquired(statusEnquired);
        response.setUserId(userId);

        when(lotteryService.updateTicket(id, request)).thenReturn(response);
        this.mockMvc.perform(put("/lotteryapi/v1/ticket/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void updateTicketInvalidUserId() throws Exception {
        String id = "id1";
        String userId = "";
        boolean statusEnquired = false;

        LotteryTicketRequest request = new LotteryTicketRequest();
        request.setLines(List.of("000", "012", "001", "002"));
        request.setUserId(userId);

        this.mockMvc.perform(put("/lotteryapi/v1/ticket/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateTicketInvalidTicketLines() throws Exception {
        String id = "id1";
        String userId = "user1";
        boolean statusEnquired = false;

        LotteryTicketRequest request = new LotteryTicketRequest();
        request.setUserId(userId);

        this.mockMvc.perform(put("/lotteryapi/v1/ticket/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        request.setLines(List.of("0000", "012222"));

        this.mockMvc.perform(put("/lotteryapi/v1/ticket/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateTicketIdNotFoundInDB() throws Exception {
        String id = "id1";
        String userId = "user1";

        LotteryTicketRequest request = new LotteryTicketRequest();
        request.setLines(List.of("000", "012"));
        request.setUserId(userId);


        when(lotteryService.updateTicket(anyString(), any(LotteryTicketRequest.class))).thenThrow(new NoSuchElementException());
        this.mockMvc.perform(put("/lotteryapi/v1/ticket/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateTicketIdDifferentUser() throws Exception {
        String id = "id1";
        String userId = "user1";

        LotteryTicketRequest request = new LotteryTicketRequest();
        request.setLines(List.of("000", "012"));
        request.setUserId(userId);


        when(lotteryService.updateTicket(anyString(), any(LotteryTicketRequest.class))).thenThrow(new AccessDeniedException("Access denied"));
        this.mockMvc.perform(put("/lotteryapi/v1/ticket/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void updateTicketStatusValidScenario() throws Exception {
        String id = "id1";
        String userId = "user1";
        boolean statusEnquired = false;

        LotteryTicketRequest request = new LotteryTicketRequest();
        request.setLines(List.of("000", "012"));
        request.setUserId(userId);

        LotteryTicket response = new LotteryTicket();
        response.setCreatedDateTime(new Date());
        response.setUpdatedDateTime(new Date());
        response.setId(id);
        response.setLines(List.of(
                new Line("000", 5),
                new Line("012", 1)));
        response.setStatusEnquired(statusEnquired);
        response.setUserId(userId);

        when(lotteryService.updateTicketStatus(id, null, request)).thenReturn(response);
        this.mockMvc.perform(put("/lotteryapi/v1/status/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void updateTicketStatusInvalidUserId() throws Exception {
        String id = "id1";
        String userId = "";
        boolean statusEnquired = false;

        LotteryTicketRequest request = new LotteryTicketRequest();
        request.setLines(List.of("000", "012", "001", "002"));
        request.setUserId(userId);

        this.mockMvc.perform(put("/lotteryapi/v1/status/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void updateTicketStatusIdNotFoundInDB() throws Exception {
        String id = "id1";
        String userId = "user1";

        LotteryTicketRequest request = new LotteryTicketRequest();
        request.setLines(List.of("000", "012"));
        request.setUserId(userId);


        when(lotteryService.updateTicketStatus(anyString(), anyString(), any(LotteryTicketRequest.class))).thenThrow(new NoSuchElementException());
        this.mockMvc.perform(put("/lotteryapi/v1/status/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .param("sortDir", "asc")
                .content(mapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateTicketStatusIdDifferentUser() throws Exception {
        String id = "id1";
        String userId = "user1";

        LotteryTicketRequest request = new LotteryTicketRequest();
        request.setLines(List.of("000", "012"));
        request.setUserId(userId);


        when(lotteryService.updateTicketStatus(anyString(), anyString(), any(LotteryTicketRequest.class))).thenThrow(new AccessDeniedException("Access denied"));
        this.mockMvc.perform(put("/lotteryapi/v1/status/" + id)
                .param("sortDir", "asc")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
