package com.example.lotterymgmtapi.service;

import com.example.lotterymgmtapi.entity.LotteryTicketRequest;
import com.example.lotterymgmtapi.entity.LotteryTicketResponse;
import com.example.lotterymgmtapi.model.Line;
import com.example.lotterymgmtapi.model.LotteryTicket;
import com.example.lotterymgmtapi.repository.LotteryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.AccessDeniedException;
import java.text.DateFormat;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class LotteryServiceTest {
    @Mock
    private LotteryRepository lotteryRepository;

    @InjectMocks
    private LotteryService lotteryService;

    @Test
    public void testGetAllTickets() {
        List<Line> lines = List.of(
                new Line("000", 5),
                new Line("002", 10));
        String userId = "user1";
        boolean statusEnquired = false;

        List<LotteryTicket> lotteryTicketList = List.of(
                new LotteryTicket(lines, userId, new Date(), new Date(), statusEnquired)
        );

        given(lotteryRepository.findAll()).willReturn(lotteryTicketList);

        List<LotteryTicketResponse> lotteryTicketListReturned = lotteryService.getAllTickets();
        assertEquals(lotteryTicketListReturned.size(), 1);
    }

    @Test
    public void testGetTicketByIdValidScenario() {
        String id = "id1";
        String userId = "user1";
        List<Line> lines = List.of(
                new Line("000", 5),
                new Line("002", 10));
        boolean statusEnquired = false;

        LotteryTicket lotteryTicket = new LotteryTicket(lines, userId, new Date(), new Date(), statusEnquired);

        given(lotteryRepository.findById(id)).willReturn(Optional.of(lotteryTicket));

        LotteryTicketResponse lotteryTicketReturned = lotteryService.getTicketById(id);
        assertEquals(lotteryTicketReturned.getLines().size(), 2);
        assertEquals(lotteryTicketReturned.getUserId(), userId);
        assertEquals(lotteryTicketReturned.isStatusEnquired(), statusEnquired);
        assertNotNull(lotteryTicketReturned.getCreatedDateTime());
        assertNotNull(lotteryTicketReturned.getUpdatedDateTime());
    }

    @Test
    public void testGetTicketByIdNotPresentInDB() {
        String id = "id1";
        given(lotteryRepository.findById(id)).willReturn(Optional.empty());
        try {
            lotteryService.getTicketById(id);
        } catch (NoSuchElementException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testSaveTicket() {
        String id = "id1";
        String userId = "user1";
        List<Line> lines = List.of(
                new Line("000", 5),
                new Line("012", 1),
                new Line("001", 0),
                new Line("002", 10));
        boolean statusEnquired = false;

        LotteryTicket lotteryTicket = new LotteryTicket(lines, userId, new Date(), new Date(), statusEnquired);

        LotteryTicketRequest request = new LotteryTicketRequest(List.of("000", "012" , "001" , "002"), userId);

        given(lotteryRepository.save(any(LotteryTicket.class))).willReturn(lotteryTicket);

        LotteryTicketResponse lotteryTicketReturned = lotteryService.saveTicket(request);
        assertEquals(lotteryTicketReturned.getLines().size(), 4);
        assertEquals(lotteryTicketReturned.getUserId(), userId);
        assertEquals(lotteryTicketReturned.isStatusEnquired(), statusEnquired);
        assertNotNull(lotteryTicketReturned.getCreatedDateTime());
        assertNotNull(lotteryTicketReturned.getUpdatedDateTime());
    }

    @Test
    public void testUpdateTicketValidScenario() throws AccessDeniedException {
        String id = "id1";
        String userId = "user1";
        List<Line> lines = List.of(
                new Line("000", 5),
                new Line("002", 10));
        boolean statusEnquired = false;

        LotteryTicket lotteryTicket = new LotteryTicket(lines, userId, new Date(), new Date(), statusEnquired);

        LotteryTicketRequest request = new LotteryTicketRequest(List.of("000", "002"), userId);

        given(lotteryRepository.findById(id)).willReturn(Optional.of(lotteryTicket));
        given(lotteryRepository.save(any(LotteryTicket.class))).willReturn(lotteryTicket);

        LotteryTicketResponse lotteryTicketReturned = lotteryService.updateTicket(id, request);
        assertEquals(lotteryTicketReturned.getLines().size(), 2);
        assertEquals(lotteryTicketReturned.getUserId(), userId);
        assertEquals(lotteryTicketReturned.isStatusEnquired(), statusEnquired);
        assertNotNull(lotteryTicketReturned.getCreatedDateTime());
        assertNotNull(lotteryTicketReturned.getUpdatedDateTime());
    }

    @Test
    public void testUpdateTicketIdNotPresentInDB() {
        String id = "id1";
        LotteryTicketRequest request = new LotteryTicketRequest(List.of("000", "002"), id);
        given(lotteryRepository.findById(id)).willReturn(Optional.empty());
        try {
            lotteryService.updateTicket(id, request);
        } catch (NoSuchElementException | AccessDeniedException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testUpdateTicketInvalidUser() {
        String id = "id1";
        String userId = "user1";
        String userIdAnother = "user2";
        List<Line> lines = List.of(
                new Line("000", 5),
                new Line("002", 10));
        boolean statusEnquired = false;

        LotteryTicket lotteryTicket = new LotteryTicket(lines, userId, new Date(), new Date(), statusEnquired);

        LotteryTicketRequest request = new LotteryTicketRequest(List.of("000", "002"), userIdAnother);

        given(lotteryRepository.findById(id)).willReturn(Optional.of(lotteryTicket));
        try {
            lotteryService.updateTicket(id, request);
        } catch (AccessDeniedException e) {
            assertTrue(e.getMessage().equalsIgnoreCase("Access Denied"));;
        }
    }

    @Test
    public void testUpdateTicketAfterStatusCheck() {
        String id = "id1";
        String userId = "user1";
        List<Line> lines = List.of(
                new Line("000", 5),
                new Line("002", 10));
        boolean statusEnquired = true;

        LotteryTicket lotteryTicket = new LotteryTicket(lines, userId, new Date(), new Date(), statusEnquired);

        LotteryTicketRequest request = new LotteryTicketRequest(List.of("000", "002"), userId);

        given(lotteryRepository.findById(id)).willReturn(Optional.of(lotteryTicket));

        try {
            lotteryService.updateTicket(id, request);
        } catch (AccessDeniedException e) {
            assertTrue(e.getMessage().equalsIgnoreCase("Updates not possible after status check"));;
        }
    }

    @Test
    public void testUpdateTicketStatusValidScenarioSortAscending() throws AccessDeniedException {
        String id = "id1";
        String userId = "user1";
        List<Line> lines = List.of(
                new Line("000", 5),
                new Line("002", 10));
        boolean statusEnquired = false;

        LotteryTicket lotteryTicket = new LotteryTicket(lines, userId, new Date(), new Date(), statusEnquired);

        LotteryTicketRequest request = new LotteryTicketRequest(List.of("000", "002"), userId);

        given(lotteryRepository.findById(id)).willReturn(Optional.of(lotteryTicket));
        given(lotteryRepository.save(any(LotteryTicket.class))).willReturn(lotteryTicket);

        LotteryTicket lotteryTicketReturned = lotteryService.updateTicketStatus(id, "asc",request);
        assertEquals(lotteryTicketReturned.getLines().size(), 2);
        assertEquals(lotteryTicketReturned.getLines().get(0).getNumbers(), "000");
        assertEquals(lotteryTicketReturned.getUserId(), userId);
        assertEquals(lotteryTicketReturned.getStatusEnquired(), true);
        assertNotNull(lotteryTicketReturned.getCreatedDateTime());
        assertNotNull(lotteryTicketReturned.getUpdatedDateTime());
    }

    @Test
    public void testUpdateTicketStatusValidScenarioSortDesending() throws AccessDeniedException {
        String id = "id1";
        String userId = "user1";
        List<Line> lines = List.of(
                new Line("000", 5),
                new Line("002", 10));
        boolean statusEnquired = false;

        LotteryTicket lotteryTicket = new LotteryTicket(lines, userId, new Date(), new Date(), statusEnquired);

        LotteryTicketRequest request = new LotteryTicketRequest(List.of("000", "002"), userId);

        given(lotteryRepository.findById(id)).willReturn(Optional.of(lotteryTicket));
        given(lotteryRepository.save(any(LotteryTicket.class))).willReturn(lotteryTicket);

        LotteryTicket lotteryTicketReturned = lotteryService.updateTicketStatus(id, "desc",request);
        assertEquals(lotteryTicketReturned.getLines().size(), 2);
        assertEquals(lotteryTicketReturned.getLines().get(0).getNumbers(), "002");
        assertEquals(lotteryTicketReturned.getUserId(), userId);
        assertEquals(lotteryTicketReturned.getStatusEnquired(), true);
        assertNotNull(lotteryTicketReturned.getCreatedDateTime());
        assertNotNull(lotteryTicketReturned.getUpdatedDateTime());
    }

    @Test
    public void testUpdateTicketStatusIdNotPresentInDB() {
        String id = "id1";
        LotteryTicketRequest request = new LotteryTicketRequest(List.of("000", "002"), id);
        given(lotteryRepository.findById(id)).willReturn(Optional.empty());
        try {
            lotteryService.updateTicketStatus(id, null,request);
        } catch (NoSuchElementException | AccessDeniedException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testUpdateStatusTicketInvalidUser() {
        String id = "id1";
        String userId = "user1";
        String userIdAnother = "user2";
        List<Line> lines = List.of(
                new Line("000", 5),
                new Line("002", 10));
        boolean statusEnquired = false;

        LotteryTicket lotteryTicket = new LotteryTicket(lines, userId, new Date(), new Date(), statusEnquired);

        LotteryTicketRequest request = new LotteryTicketRequest(List.of("000", "002"), userIdAnother);

        given(lotteryRepository.findById(id)).willReturn(Optional.of(lotteryTicket));
        try {
            lotteryService.updateTicketStatus(id,null, request);
        } catch (AccessDeniedException e) {
            assertTrue(e.getMessage().equalsIgnoreCase("Access Denied"));;
        }
    }

    @Test
    public void testUpdateTicketStatusAfterStatusCheck() {
        String id = "id1";
        String userId = "user1";
        List<Line> lines = List.of(
                new Line("000", 5),
                new Line("002", 10));
        boolean statusEnquired = true;

        LotteryTicket lotteryTicket = new LotteryTicket(lines, userId, new Date(), new Date(), statusEnquired);

        LotteryTicketRequest request = new LotteryTicketRequest(List.of("000", "002"), userId);

        given(lotteryRepository.findById(id)).willReturn(Optional.of(lotteryTicket));

        try {
            lotteryService.updateTicketStatus(id,null, request);
        } catch (AccessDeniedException e) {
            assertTrue(e.getMessage().equalsIgnoreCase("Updates not possible after status check"));;
        }
    }
}
