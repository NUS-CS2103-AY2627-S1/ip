package xue.model;

import xue.XueException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * Represents a task in Xue's task list.
 */
public class Task {
    private static final String INVALID_DATE_MESSAGE =
            "That date is invalid. Use yyyy-MM-dd or yyyy-MM-dd HHmm.";
    private static final DateTimeFormatter INPUT_DATE_TIME = DateTimeFormatter
            .ofPattern("uuuu-MM-dd HHmm").withResolverStyle(ResolverStyle.STRICT);
    /** The one-letter category used when the task is displayed and saved. */
    private final String type;
    /** The text describing the work to be done. */
    private final String description;
    /** The original event start text, or {@code null} for non-events. */
    private final String from;
    /** The original deadline or event end text, or {@code null} for todos. */
    private final String to;
    /** Parsed start value used to format numeric event dates. */
    private final LocalDateTime fromDateTime;
    /** Parsed end value used to format numeric deadlines and event dates. */
    private final LocalDateTime toDateTime;
    /** Whether this task has been completed. */
    private boolean isDone;

    /**
     * Creates an incomplete task with the given description.
     *
     * @param description the task description
     */
    public Task(String description) {
        this("T", description, null, null);
    }

    /** Creates a dated task with the supplied display details. */
    public Task(String type, String description, String from, String to) {
        if (!"T".equals(type) && !"D".equals(type) && !"E".equals(type)) {
            throw new XueException("A task must be a todo, deadline, or event.");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new XueException("The description of a todo cannot be empty. I cannot read your mind!");
        }
        this.type = type;
        this.description = description;
        this.from = from;
        this.to = to;
        this.fromDateTime = "E".equals(type) ? parseDateTime(from) : null;
        this.toDateTime = "T".equals(type) ? null : parseDateTime(to);
        if ("E".equals(type) && fromDateTime != null && toDateTime != null
                && toDateTime.isBefore(fromDateTime)) {
            throw new XueException("An event cannot end before it starts.");
        }
        this.isDone = false;
    }

    /** Creates a task while restoring its saved completion status. */
    public Task(String type, String description, String from, String to, boolean isDone) {
        this(type, description, from, to);
        this.isDone = isDone;
    }

    /** Creates a copy of an existing task, including its completion status. */
    public Task(Task other) {
        this(other.type, other.description, other.from, other.to, other.isDone);
    }

    /** Marks this task as done. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as not done. */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns the status icon used when displaying this task.
     *
     * @return {@code X} for a completed task, otherwise a space
     */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /**
     * Returns this task's description.
     *
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /** Returns the one-letter task type shown in the list. */
    public String getType() {
        return type;
    }

    /** Returns the start time of an event, if applicable. */
    public String getFrom() {
        return from;
    }

    /** Returns the end time of an event or deadline, if applicable. */
    public String getTo() {
        return to;
    }

    /** Returns whether this task is complete. */
    public boolean isDone() {
        return isDone;
    }

    /** Returns the optional date/time suffix shown in the list. */
    public String getDateTimeDescription() {
        if ("D".equals(type)) {
            return " (by: " + formatDateTime(to, toDateTime) + ")";
        }
        if ("E".equals(type)) {
            return " (from: " + formatDateTime(from, fromDateTime) + " to: "
                    + formatDateTime(to, toDateTime) + ")";
        }
        return "";
    }

    /** Returns the parsed deadline or event start, when the input used a numeric date. */
    public LocalDateTime getFromDateTime() {
        return fromDateTime;
    }

    /** Returns the parsed deadline or event end, when the input used a numeric date. */
    public LocalDateTime getToDateTime() {
        return toDateTime;
    }

    /**
     * Parses numeric dates strictly, rejecting malformed numeric dates and missing values.
     * Text dates remain display-only because expressions such as Sunday have no fixed date.
     */
    private static LocalDateTime parseDateTime(String value) {
        if (value == null || value.isBlank()) {
            throw new XueException(INVALID_DATE_MESSAGE);
        }
        if (!value.matches("\\d{4}-\\d{2}-\\d{2}( \\d{4})?")) {
            // Numeric date separators distinguish date typos from supported text such as 2pm.
            if (value.matches(".*\\d\\s*[-/.]\\s*\\d.*") || value.matches("[\\d\\s]+")) {
                throw new XueException(INVALID_DATE_MESSAGE);
            }
            return null;
        }
        try {
            return value.length() == 10
                    ? LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay()
                    : LocalDateTime.parse(value, INPUT_DATE_TIME);
        } catch (DateTimeParseException e) {
            throw new XueException(INVALID_DATE_MESSAGE);
        }
    }

    /** Formats a parsed date while preserving non-numeric date text unchanged. */
    private static String formatDateTime(String original, LocalDateTime parsed) {
        // A parsed value can only exist when the original text was a supported numeric date.
        assert original != null;
        if (parsed == null) {
            return original;
        }
        String date = parsed.format(DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH));
        return original.length() > 10
                ? date + " " + parsed.format(DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH))
                        .toUpperCase(Locale.ENGLISH)
                : date;
    }

}
