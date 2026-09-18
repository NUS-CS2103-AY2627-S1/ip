package com.benthecat.kachow.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;

import com.benthecat.kachow.parser.DateTimeParser;

/**
 * Represents a task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    private final DateTimeParser.ParsedDateTime dueDateTime;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description Text describing the task.
     * @param by Date by which the task must be completed.
     */
    public Deadline(String description, LocalDate by) {
        this(description, new DateTimeParser.ParsedDateTime(by), false);
    }

    /**
     * Creates an incomplete deadline task with a due date and time.
     *
     * @param description Text describing the task.
     * @param by Date and time by which the task must be completed.
     */
    public Deadline(String description, LocalDateTime by) {
        this(description, new DateTimeParser.ParsedDateTime(by), false);
    }

    /**
     * Creates a deadline with a restored completion status.
     *
     * @param description Text describing the task.
     * @param by Date by which the task must be completed.
     * @param isDone Whether the task has been completed.
     */
    public Deadline(String description, LocalDate by, boolean isDone) {
        this(description, new DateTimeParser.ParsedDateTime(by), isDone);
    }

    /**
     * Creates a deadline with a due date, due time, and restored completion status.
     *
     * @param description Text describing the task.
     * @param by Date and time by which the task must be completed.
     * @param isDone Whether the task has been completed.
     */
    public Deadline(String description, LocalDateTime by, boolean isDone) {
        this(description, new DateTimeParser.ParsedDateTime(by), isDone);
    }

    /**
     * Creates a deadline from a value produced by the common date/time parser.
     *
     * @param description Text describing the task.
     * @param by Parsed due date and optional time.
     */
    public Deadline(String description, DateTimeParser.ParsedDateTime by) {
        this(description, by, false);
    }

    /**
     * Creates a deadline from a parsed value with a restored completion status.
     *
     * @param description Text describing the task.
     * @param by Parsed due date and optional time.
     * @param isDone Whether the task has been completed.
     */
    public Deadline(String description, DateTimeParser.ParsedDateTime by, boolean isDone) {
        super(description, isDone);
        dueDateTime = Objects.requireNonNull(by);
    }

    /**
     * Returns the deadline's due date without display formatting.
     *
     * @return Deadline date.
     */
    public LocalDate getBy() {
        return dueDateTime.date();
    }

    /**
     * Returns the deadline's due time when one was supplied.
     *
     * @return Optional deadline time.
     */
    public Optional<LocalTime> getTime() {
        return dueDateTime.time();
    }

    /**
     * Returns the full value produced by the common date/time parser.
     *
     * @return Parsed due date and optional time.
     */
    public DateTimeParser.ParsedDateTime getDueDateTime() {
        return dueDateTime;
    }

    /** {@inheritDoc} */
    @Override
    public boolean occursOn(LocalDate date) {
        return dueDateTime.date().equals(date);
    }

    /** {@inheritDoc} */
    @Override
    public String getStatusText() {
        return "[D]" + super.getStatusText() + " (by: " + DateTimeParser.format(dueDateTime) + ")";
    }
}
