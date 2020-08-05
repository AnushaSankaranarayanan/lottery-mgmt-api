package com.example.lotterymgmtapi.model;

import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.List;

/**
 * The type Lottery ticket.
 */
public class LotteryTicket {
    @Id
    private String id;

    private List<Line> lines;

    private String userId;

    private Date createdDateTime;

    private Date updatedDateTime;

    private boolean statusEnquired;

    /**
     * Instantiates a new Lottery ticket.
     */
    public LotteryTicket() {
    }

    /**
     * Instantiates a new Lottery ticket.
     *
     * @param lines           the lines
     * @param userId          the user id
     * @param createdDateTime the created date time
     * @param updatedDateTime the updated date time
     * @param statusEnquired  the status enquired
     */
    public LotteryTicket(List<Line> lines, String userId, Date createdDateTime, Date updatedDateTime, boolean statusEnquired) {
        this.lines = lines;
        this.userId = userId;
        this.createdDateTime = createdDateTime;
        this.updatedDateTime = updatedDateTime;
        this.statusEnquired = statusEnquired;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets lines.
     *
     * @return the lines
     */
    public List<Line> getLines() {
        return lines;
    }

    /**
     * Sets lines.
     *
     * @param lines the lines
     */
    public void setLines(List<Line> lines) {
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

    /**
     * Gets created date time.
     *
     * @return the created date time
     */
    public Date getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * Sets created date time.
     *
     * @param createdDateTime the created date time
     */
    public void setCreatedDateTime(Date createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Gets status enquired.
     *
     * @return the status enquired
     */
    public boolean getStatusEnquired() {
        return statusEnquired;
    }

    /**
     * Sets status enquired.
     *
     * @param statusEnquired the status enquired
     */
    public void setStatusEnquired(boolean statusEnquired) {
        this.statusEnquired = statusEnquired;
    }

    /**
     * Gets updated date time.
     *
     * @return the updated date time
     */
    public Date getUpdatedDateTime() {
        return updatedDateTime;
    }

    /**
     * Sets updated date time.
     *
     * @param updatedDateTime the updated date time
     */
    public void setUpdatedDateTime(Date updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
    }
}
