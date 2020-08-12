package com.example.lotterymgmtapi.service;

import com.example.lotterymgmtapi.entity.LotteryTicketRequest;
import com.example.lotterymgmtapi.entity.LotteryTicketResponse;
import com.example.lotterymgmtapi.model.Line;
import com.example.lotterymgmtapi.model.LotteryTicket;
import com.example.lotterymgmtapi.repository.LotteryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The type Lottery service.
 */
@Service
public class LotteryService {

    @Autowired
    private LotteryRepository lotteryRepository;

    /**
     * Gets all tickets.
     *
     * @return the all tickets
     */
    public List<LotteryTicketResponse> getAllTickets() {
        List<LotteryTicket> lotteryTickets = lotteryRepository.findAll();
        // The Response model is different from the DB model because
        // the status field should not be send back in the response
        List<LotteryTicketResponse> responseList = lotteryTickets.stream()
                .map(lotteryTicket -> prepareLotteryResponse(lotteryTicket))
                .collect(Collectors.toList());
        return responseList;
    }


    /**
     * Gets ticket by id.
     *
     * @param id the id
     * @return the ticket by id
     */
    public LotteryTicketResponse getTicketById(String id) {
        LotteryTicket lotteryTicket = lotteryRepository.findById(id).orElseThrow();
        return prepareLotteryResponse(lotteryTicket);
    }


    /**
     * Save ticket
     *
     * @param ticketRequest the ticket request
     * @return the lottery ticket response
     */
    public LotteryTicketResponse saveTicket(LotteryTicketRequest ticketRequest) {
        LotteryTicket lotteryTicket = prepareLotteryTicketFromRequest(ticketRequest);
        LotteryTicketResponse response = prepareLotteryResponse(lotteryRepository.save(lotteryTicket));
        return response;
    }

    /**
     * Update ticket lottery
     *
     * @param id            the id
     * @param ticketRequest the ticket request
     * @return the lottery ticket response
     * @throws AccessDeniedException the access denied exception
     */
    public LotteryTicketResponse updateTicket(String id, LotteryTicketRequest ticketRequest) throws AccessDeniedException {
        LotteryTicket _lotteryTicket = findTicketById(id).orElseThrow();
        if (!_lotteryTicket.getUserId().equalsIgnoreCase(ticketRequest.getUserId())) {
            throw new AccessDeniedException("Access Denied");
        }
        if (_lotteryTicket.getStatusEnquired()) {
            throw new AccessDeniedException("Updates not possible after status check");
        }
        _lotteryTicket.setLines(ticketRequest.getLines()
                .stream()
                .map(number -> new Line(number, computeResult(number)))
                .collect(Collectors.toList()));
        _lotteryTicket.setUpdatedDateTime(new Date());
        LotteryTicketResponse response = prepareLotteryResponse(lotteryRepository.save(_lotteryTicket));
        return response;
    }

    /**
     * Update ticket status of the lottery ticket.
     *
     * @param id            the id
     * @param sortDir       the sort dir
     * @param ticketRequest the ticket request
     * @return the lottery ticket
     * @throws AccessDeniedException the access denied exception
     */
    public LotteryTicket updateTicketStatus(String id, String sortDir, LotteryTicketRequest ticketRequest) throws AccessDeniedException {
        LotteryTicket _lotteryTicket = findTicketById(id).orElseThrow();
        if (!_lotteryTicket.getUserId().equalsIgnoreCase(ticketRequest.getUserId())) {
            throw new AccessDeniedException("Access Denied");
        }
        if (_lotteryTicket.getStatusEnquired()) {
            throw new AccessDeniedException("Updates not possible after status check");
        }
        _lotteryTicket.setStatusEnquired(true);
        _lotteryTicket.setUpdatedDateTime(new Date());
        LotteryTicket lotteryTicket = lotteryRepository.save(_lotteryTicket);
        LotteryTicket sortedTickets = sortTicketsByLines(lotteryTicket, sortDir);
        return sortedTickets;
    }

    /**
     * Find ticket by Id
     */
    private Optional<LotteryTicket> findTicketById(String id) {
        return lotteryRepository.findById(id);
    }


    /**
     * Sort Tickets based on Sort Direction.
     * If none specified, sorts by descending order.
     */
    private LotteryTicket sortTicketsByLines(LotteryTicket lotteryTicket, String sortDir) {
        List<Line> sortedLines;
        if ("asc".equalsIgnoreCase(sortDir)) {
            sortedLines = lotteryTicket.getLines().stream()
                    .sorted(Comparator.comparingInt(Line::getResult))
                    .collect(Collectors.toList());
            lotteryTicket.setLines(sortedLines);
        } else {
            sortedLines = lotteryTicket.getLines().stream()
                    .sorted(Comparator.comparingInt(Line::getResult)
                            .reversed()).collect(Collectors.toList());
            lotteryTicket.setLines(sortedLines);
        }
        return lotteryTicket;
    }

    /**
     * Prepare  DB Model (LotteryTicket) based on the incoming request(LotteryTicketRequest)
     */
    private LotteryTicket prepareLotteryTicketFromRequest(LotteryTicketRequest ticketRequest) {
        LotteryTicket lotteryTicket = new LotteryTicket(
                ticketRequest.getLines().stream().map(number -> new Line(number, computeResult(number))).collect(Collectors.toList()),
                ticketRequest.getUserId(),
                new Date(),
                new Date(),
                false
        );
        return lotteryTicket;
    }

    /**
     * Prepare outgoing response(LotteryTicketResponse) based on the DB Model(LotteryTicket)
     */
    private LotteryTicketResponse prepareLotteryResponse(LotteryTicket lotteryTicket) {
        LotteryTicketResponse lotteryTicketResponse = new LotteryTicketResponse(
                lotteryTicket.getId(),
                lotteryTicket.getLines().stream().map(line -> line.getNumbers()).collect(Collectors.toList()),
                lotteryTicket.getUserId(),
                lotteryTicket.getCreatedDateTime(),
                lotteryTicket.getUpdatedDateTime(),
                lotteryTicket.getStatusEnquired()
        );
        return lotteryTicketResponse;
    }

    /**
     * Compute Result of the Lottery Lines based on the rules below:
     * You have a series of lines on a ticket with 3 numbers, each of which has a value of 0, 1, or
     * 2. For each ticket if the sum of the values on a line is 2, the result for that line is 10.
     * Otherwise if they are all the same, the result is 5. Otherwise so long as both 2nd and 3rd
     * numbers are different from the 1st, the result is 1. Otherwise the result is 0.
     */
    private int computeResult(String numbersInLine) {
        int result;
        int num1 = Integer.parseInt(numbersInLine.substring(0, 1));
        int num2 = Integer.parseInt(numbersInLine.substring(1, 2));
        int num3 = Integer.parseInt(numbersInLine.substring(2, 3));

        if (num1 + num2 + num3 == 2) {
            result = 10;
        } else if ((num1 == num2) && (num2 == num3)) {
            result = 5;
        } else if ((num1 != num2) && (num1 != num3)) {
            result = 1;
        } else {
            result = 0;
        }
        return result;
    }


}

