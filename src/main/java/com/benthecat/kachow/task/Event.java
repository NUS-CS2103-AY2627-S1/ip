package com.benthecat.kachow.task;

import java.time.LocalDate;
import java.util.Objects;

import com.benthecat.kachow.parser.DateTimeParser;

/**
 * Represents a task that takes place between specified start and end times.
 */
public class Event extends Task {
    private final DateTimeParser.ParsedDateTime startDateTime;
    private final DateTimeParser.ParsedDateTime endDateTime;

    /**
     * Creates an incomplete event task.
     *
     * @param description Text describing the event.
     * @param from Parsed date and optional time at which the event starts.
     * @param to Parsed date and optional time at which the event ends.
     * @throws IllegalArgumentException If the event does not end after it starts.
     */
    public Event(String description, DateTimeParser.ParsedDateTime from, DateTimeParser.ParsedDateTime to) {
        this(description, from, to, false);
    }

    /**
     * Creates an event with a restored completion status.
     *
     * @param description Text describing the event.
     * @param from Parsed date and optional time at which the event starts.
     * @param to Parsed date and optional time at which the event ends.
     * @param isDone Whether the event has been completed.
     * @throws IllegalArgumentException If the event does not end after it starts.
     */
    public Event(String description, DateTimeParser.ParsedDateTime from, DateTimeParser.ParsedDateTime to,
            boolean isDone) {
        super(description, isDone);
        startDateTime = Objects.requireNonNull(from);
        endDateTime = Objects.requireNonNull(to);
        if (!to.toLocalDateTime().isAfter(from.toLocalDateTime())) {
            throw new IllegalArgumentException("An event must end after it starts.");
        }
    }

    /**
     * Returns the event's parsed start without display formatting.
     *
     * @return Parsed event start.
     */
    public DateTimeParser.ParsedDateTime getFrom() {
        return startDateTime;
    }

    /**
     * Returns the event's parsed end without display formatting.
     *
     * @return Parsed event end.
     */
    public DateTimeParser.ParsedDateTime getTo() {
        return endDateTime;
    }

    /** {@inheritDoc} */
    @Override
    public boolean occursOn(LocalDate date) {
        return !date.isBefore(startDateTime.date()) && !date.isAfter(endDateTime.date());
    }

    /** {@inheritDoc} */
    @Override
    public String getStatusText() {
        return "[E]" + super.getStatusText()
                + " (from: " + DateTimeParser.format(startDateTime)
                + " to: " + DateTimeParser.format(endDateTime) + ")";
    }
}
