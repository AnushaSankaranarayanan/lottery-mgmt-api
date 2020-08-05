package com.example.lotterymgmtapi.entity;

import java.util.List;


/**
 * The type Lottery ticket request.
 */
public class LotteryTicketRequest {
    private List<String> lines;

    private String userId;

    /**
     * Instantiates a new Lottery ticket request.
     */
    public LotteryTicketRequest() {
    }

    /**
     * Instantiates a new Lottery ticket request.
     *
     * @param lines  the lines
     * @param userId the user id
     */
    public LotteryTicketRequest(List<String> lines, String userId) {
        this.lines = lines;
        this.userId = userId;
    }

    /**
     * Gets lines.
     *
     * @return the lines
     */
    public List<String> getLines() {
        return lines;
    }

    /**
     * Sets lines.
     *
     * @param lines the lines
     */
    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    /**
     * Gets user id.
     *
     * @return the user id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets user id.
     *
     * @param userId the user id
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }
}
