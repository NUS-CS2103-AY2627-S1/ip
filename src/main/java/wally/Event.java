package wally;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * Stores a task with a description, start date-time, and end date-time.
 */
public class Event extends Task {
    private final LocalDateTime starting;
    private final LocalDateTime ending;

    /**
     * Creates an event task with the specified name, start, and end times.
     *
     * @param name Name of the task.
     * @param starting Start time in the expected date-time format.
     * @param ending End time in the expected date-time format.
     * @throws InvalidEventException If the start is not strictly before the end.
     */
    public Event(String name, String starting, String ending) throws InvalidEventException {
        super(name);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm")
                .withResolverStyle(ResolverStyle.STRICT);
        this.starting = LocalDateTime.parse(starting, formatter);
        this.ending = LocalDateTime.parse(ending, formatter);
        if (!this.starting.isBefore(this.ending)) {
            throw new InvalidEventException("Event start must be before end.");
        }
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: "
                + this.starting.format(DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm", Locale.ENGLISH))
                + " to: " + this.ending.format(DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm", Locale.ENGLISH)) + ")";
    }

    @Override
    public String getCommand() {
        return "event " + super.getCommand() + " /from "
                + this.starting.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + " /to "
                + this.ending.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    @Override
    public LocalDateTime getScheduleDateTime() {
        return starting;
    }
}
