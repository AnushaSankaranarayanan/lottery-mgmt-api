package com.example.lotterymgmtapi.controller;

import com.example.lotterymgmtapi.entity.LotteryTicketRequest;
import com.example.lotterymgmtapi.entity.LotteryTicketResponse;
import com.example.lotterymgmtapi.model.LotteryTicket;
import com.example.lotterymgmtapi.service.LotteryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@Api(value = "Controller handles all end points related to lottery management API")
@RequestMapping("/lotteryapi/v1")
public class LotteryController {

    Logger logger = LoggerFactory.getLogger(LotteryController.class);

    @Autowired
    LotteryService lotteryService;

    @ApiOperation(value = "List all tickets", response = LotteryTicketResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list"),
            @ApiResponse(code = 204, message = "No data returned")
    }
    )
    @GetMapping("/ticket")
    public ResponseEntity<List<LotteryTicketResponse>> getAllTickets() {
        List<LotteryTicketResponse> tickets = new ArrayList<LotteryTicketResponse>();
        lotteryService.getAllTickets().forEach(tickets::add);
        if (tickets.isEmpty()) {
            logger.info("No Lottery tickets");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(tickets, HttpStatus.OK);

    }

    @ApiOperation(value = "Get lottery ticket by Id", response = LotteryTicketResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved ticket"),
            @ApiResponse(code = 404, message = "Ticket not found")
    }
    )
    @GetMapping("/ticket/{id}")
    public ResponseEntity<LotteryTicketResponse> getTicketById(@PathVariable("id") String id) {
        try {
            LotteryTicketResponse ticketById = lotteryService.getTicketById(id);
            return new ResponseEntity<>(ticketById, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            logger.error("Lottery ticket not found in DB for id: " + id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "Save/Create a lottery ticket", response = LotteryTicketResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created ticket"),
            @ApiResponse(code = 400, message = "request body invalid")
    }
    )
    @PostMapping("/ticket")
    public ResponseEntity<LotteryTicketResponse> createTicket(@RequestBody LotteryTicketRequest lotteryTicketFromRequest) {
        if (!isUserIdValid(lotteryTicketFromRequest)) {
            logger.error("Creation of lottery ticket not allowed for user: " + lotteryTicketFromRequest.getUserId());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!areLinesValid(lotteryTicketFromRequest)) {
            logger.error("Lottery lines are invalid. Should only contain digits 0,1 and 2");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        LotteryTicketResponse createdTicket = lotteryService.saveTicket(lotteryTicketFromRequest);
        return new ResponseEntity<>(createdTicket, HttpStatus.CREATED);

    }

    @ApiOperation(value = "Update a lottery ticket by Id", response = LotteryTicketResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated ticket"),
            @ApiResponse(code = 400, message = "request body invalid"),
            @ApiResponse(code = 403, message = "Access Forbidden"),
            @ApiResponse(code = 404, message = "Ticket not found in DB")
    }
    )
    @PutMapping("/ticket/{id}")
    public ResponseEntity<LotteryTicketResponse> updateTicket(@PathVariable("id") String id, @RequestBody LotteryTicketRequest lotteryTicketFromRequest) {
        if (!isUserIdValid(lotteryTicketFromRequest)) {
            logger.error("Updation of lottery ticket not allowed for user: " + id);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!areLinesValid(lotteryTicketFromRequest)) {
            logger.error("Lottery lines are invalid. Should only contain digits 0,1 and 2");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            LotteryTicketResponse response = lotteryService.updateTicket(id, lotteryTicketFromRequest);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            logger.error("Lottery ticket not found in DB for id: " + id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (AccessDeniedException e) {
            logger.error("Updates not possible after status check");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @ApiOperation(value = "Update status of lottery ticket by Id", response = LotteryTicket.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated ticket"),
            @ApiResponse(code = 400, message = "request body invalid"),
            @ApiResponse(code = 403, message = "Access Forbidden"),
            @ApiResponse(code = 404, message = "Ticket not found in DB")
    }
    )
    @PutMapping("/status/{id}")
    public ResponseEntity<LotteryTicket> updateTicketStatus(@PathVariable("id") String id,
                                                            @RequestParam(value = "sortDir", required = false) String sortDir,
                                                            @RequestBody LotteryTicketRequest lotteryTicketFromRequest) {
        if (!isUserIdValid(lotteryTicketFromRequest)) {
            logger.error("Updation of lottery ticket not allowed for user: " + id);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            LotteryTicket updatedTicket = lotteryService.updateTicketStatus(id, sortDir, lotteryTicketFromRequest);
            return new ResponseEntity<>(updatedTicket, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            logger.error("Lottery ticket not found in DB for id: " + id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (AccessDeniedException e) {
            logger.error("Updates not possible after status check");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Check if the userId is valid in the incoming request
     *
     * @param request
     * @return the validity of userId
     */
    private boolean isUserIdValid(LotteryTicketRequest request) {
        if (!StringUtils.isEmpty(request.getUserId())) {
            return true;
        }
        return false;
    }

    /**
     * Check if the lottery lines are valid. Each line should only have 3 numbers in range 0 - 2.
     *
     * @param request -incoming request
     * @return if the lines are valid
     */
    private boolean areLinesValid(LotteryTicketRequest request) {
        String pattern = "^[0-2]{3}$";
        List<String> lines = request.getLines();
        if (!CollectionUtils.isEmpty(lines) && lines.stream().allMatch(line -> line.matches(pattern))) {
            return true;
        }
        return false;
    }
}


