package prism.task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import prism.PrismException;

/** Represents a task occurring over a start and end date and time. */
public class Event extends Task {
    private static final DateTimeFormatter INPUT_FORMATTER =
            DateTimeFormatter.ofPattern("[d/M/uuuu HHmm][uuuu-MM-dd HHmm]")
                    .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter OUTPUT_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma");
    private static final DateTimeFormatter FILE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");

    protected LocalDateTime from;
    protected LocalDateTime to;

    /** Creates an event, accepting either supported date format for both endpoints. */
    public Event(String description, String from, String to) throws PrismException {
        super(description, TaskType.EVENT);
        this.from = parseDateTime(from);
        this.to = parseDateTime(to);
    }

    /** Parses an event date and applies midnight when only a date is supplied. */
    private LocalDateTime parseDateTime(String text) throws PrismException {
        try {
            String trimmedText = text.trim();
            if (trimmedText.length() == 10) {
                trimmedText += " 0000";
            }
            return LocalDateTime.parse(trimmedText, INPUT_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new PrismException(
                    "!!! That is not a valid date or time. Please use 'd/M/yyyy HHmm' or "
                            + "'yyyy-MM-dd HHmm'.");
        }
    }

    /** Returns the event start date and time. */
    public LocalDateTime getFrom() {
        return this.from;
    }

    /** Returns the event end date and time. */
    public LocalDateTime getTo() {
        return this.to;
    }

    /** Reschedules this event's start while preserving its duration. */
    public void reschedule(String newFrom) throws PrismException {
        LocalDateTime rescheduledFrom = parseDateTime(newFrom);
        Duration duration = Duration.between(this.from, this.to);
        this.from = rescheduledFrom;
        this.to = rescheduledFrom.plus(duration);
    }

    /** Returns the serialized event representation. */
    @Override
    public String toFileFormat() {
        return super.toFileFormat() + " | " + this.from.format(FILE_FORMATTER)
                + " | " + this.to.format(FILE_FORMATTER);
    }

    /** Returns the human-readable event representation. */
    @Override
    public String toString() {
        return super.toString() + " (from: " + this.from.format(OUTPUT_FORMATTER)
                + " to: " + this.to.format(OUTPUT_FORMATTER) + ")";
    }
}
