package com.gihun456.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import com.gihun456.ErrorMessages;
import com.gihun456.GihunException;
import com.gihun456.util.DateTimeParser;

/**
 * Represents a task that must be completed by a specific date or time.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter DISPLAY_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a", Locale.ENGLISH);
    private final LocalDateTime dueDate;

    /**
     * Creates a deadline task from a user-provided date-time text.
     *
     * @param taskName Description of the task.
     * @param dueDateText Date-time string entered by the user.
     * @throws GihunException If the date format is invalid.
     */
    public Deadline(String taskName, String dueDateText) throws GihunException {
        super(taskName);
        this.dueDate = parseDueDate(dueDateText);
    }

    /**
     * Creates a deadline task from a Java date-time object.
     *
     * @param taskName Description of the task.
     * @param dueDate Exact deadline date-time.
     */
    public Deadline(String taskName, LocalDateTime dueDate) {
        super(taskName);
        assert dueDate != null : "A deadline must have a non-null due date";
        this.dueDate = dueDate;
    }

    /**
     * Returns the deadline date and time.
     *
     * @return Deadline timestamp.
     */
    public LocalDateTime getDueDate() {
        return dueDate;
    }

    /**
     * Parses a string into a LocalDateTime object using the supported date formats.
     *
     * @param dueDateText String of date with optional time.
     * @return LocalDateTime representation of the dateTime.
     * @throws GihunException If dueDateText is empty or the format is not supported.
     */
    private static LocalDateTime parseDueDate(String dueDateText) throws GihunException {
        String trimmed = dueDateText.trim();
        if (trimmed.isEmpty()) {
            throw new GihunException(ErrorMessages.DEADLINE_DATE_EMPTY);
        }

        try {
            return DateTimeParser.parseUserDateTime(trimmed);
        } catch (DateTimeParseException e) {
            throw new GihunException(ErrorMessages.INVALID_DEADLINE_FORMAT);
        }
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + DISPLAY_FORMATTER.format(dueDate) + ")";
    }
}
