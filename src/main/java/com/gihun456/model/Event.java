package com.gihun456.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import com.gihun456.ErrorMessages;
import com.gihun456.GihunException;
import com.gihun456.util.DateTimeParser;

/**
 * Represents a task that spans a start date and end date.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a", Locale.ENGLISH);
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    /**
     * Creates an event task from raw user input.
     *
     * @param taskName Description of the event.
     * @param startDateText Start date text.
     * @param endDateText End date text.
     * @throws GihunException If either date is invalid.
     */
    public Event(String taskName, String startDateText, String endDateText) throws GihunException {
        super(taskName);
        this.startDate = parseDateTime(startDateText);
        this.endDate = parseDateTime(endDateText);
        if (endDate.isBefore(startDate)) {
            throw new GihunException(ErrorMessages.EVENT_END_BEFORE_START);
        }
    }

    /**
     * Creates an event task from date-time objects.
     *
     * @param taskName Description of the event.
     * @param startDate Start date-time.
     * @param endDate End date-time.
     */
    public Event(String taskName, LocalDateTime startDate, LocalDateTime endDate) {
        super(taskName);
        assert startDate != null : "An event must have a non-null start date";
        assert endDate != null : "An event must have a non-null end date";
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Returns the event start date-time.
     *
     * @return Event start timestamp.
     */
    public LocalDateTime getStartDate() {
        return startDate;
    }

    /**
     * Returns the event end date-time.
     *
     * @return Event end timestamp.
     */
    public LocalDateTime getEndDate() {
        return endDate;
    }

    /**
     * Parses a string into a LocalDateTime object using the supported date formats.
     *
     * @param dateText String of date with optional time.
     * @return LocalDateTime representation of the dateTime.
     * @throws GihunException If dateText is empty or the format is not supported.
     */
    private static LocalDateTime parseDateTime(String dateText) throws GihunException {
        String trimmed = dateText.trim();
        if (trimmed.isEmpty()) {
            throw new GihunException(ErrorMessages.EVENT_DATE_EMPTY);
        }

        try {
            return DateTimeParser.parseUserDateTime(trimmed);
        } catch (DateTimeParseException e) {
            throw new GihunException(ErrorMessages.INVALID_EVENT_DATE_FORMAT);
        }
    }

    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + DISPLAY_FORMATTER.format(startDate)
                + " to: " + DISPLAY_FORMATTER.format(endDate) + ")";
    }
}
