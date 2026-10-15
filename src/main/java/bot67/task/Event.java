package bot67.task;

import bot67.parser.DateTimeParser;

/**
 * Represents a task that occurs during a specified time interval.
 */
public class Event extends Task {
    private final String from;
    private final String to;
    private final java.time.LocalDateTime fromDateTime;
    private final java.time.LocalDateTime toDateTime;
    private final boolean hasFromTime;
    private final boolean hasToTime;

    /**
     * Creates an event task from a user command.
     *
     * @param command the complete event command
     */
    public Event(String command) {
        super(command.substring(6, command.indexOf(" /from ")), TaskType.EVENT);
        this.from = command.substring(command.indexOf(" /from ") + 7, command.indexOf(" /to "));
        this.to = command.substring(command.indexOf(" /to ") + 5);
        this.fromDateTime = parseOrNull(from);
        this.toDateTime = parseOrNull(to);
        this.hasFromTime = hasTime(from);
        this.hasToTime = hasTime(to);
    }

    /** Returns the task description including its event interval. */
    @Override
    public String getDescription() {
        String formattedFrom = fromDateTime == null ? from : DateTimeParser.format(fromDateTime, hasFromTime);
        String formattedTo = toDateTime == null ? to : DateTimeParser.format(toDateTime, hasToTime);
        return super.getDescription() + " (from: " + formattedFrom + " to: " + formattedTo + ")";
    }

    /** Returns the task in save-file format, preserving parsed date values. */
    @Override
    public String toFileFormat() {
        String savedFrom = fromDateTime == null ? from
                : (hasFromTime ? fromDateTime.toString() : fromDateTime.toLocalDate().toString());
        String savedTo = toDateTime == null ? to
                : (hasToTime ? toDateTime.toString() : toDateTime.toLocalDate().toString());
        return super.toFileFormat() + " | " + savedFrom + " | " + savedTo;
    }

    /** Parses a date/time value, returning null for free-form input. */
    private static java.time.LocalDateTime parseOrNull(String value) {
        try {
            return DateTimeParser.parse(value);
        } catch (RuntimeException e) {
            return null;
        }
    }

    /** Checks whether an ISO date/time string includes an explicit time. */
    private static boolean hasTime(String value) {
        return value.matches("\\d{4}-\\d{2}-\\d{2}.*\\d{2}:\\d{2}");
    }
}
