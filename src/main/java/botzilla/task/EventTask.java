package botzilla.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import botzilla.BotzillaException;

/**
 * Represents a task that occurs over a specific time range,
 * e.g. "project meeting /from 2/12/2019 1400 /to 2/12/2019 1600".
 * If either date can't be parsed, it is stored and displayed as
 * plain text instead.
 */
public class EventTask extends Task {
    private final FlexibleDateTime start;
    private final FlexibleDateTime end;

    /**
     * Constructs a new EventTask, attempting to parse the given start
     * and end text as dates or date-times. Falls back to storing either
     * as raw text if it isn't date-shaped at all. Does not itself check
     * that start is before end — see {@link botzilla.parser.Parser#parseEvent},
     * which validates that once both fields are parsed.
     *
     * @param name Display name/description of the task.
     * @param start Event start text, e.g. "2/12/2019 1400" or free text.
     * @param end Event end text, e.g. "2/12/2019 1600" or free text.
     * @throws BotzillaException if {@code start} or {@code end} is date-shaped
     *                           but names an invalid calendar date or time
     *                           (see {@link DateTimeUtil#parse}).
     */
    public EventTask(String name, String start, String end) throws BotzillaException {
        super(name, TaskType.EVENT);
        this.start = FlexibleDateTime.parse(start);
        this.end = FlexibleDateTime.parse(end);
    }

    /**
     * Returns this task, including its start and end, serialized for
     * saving to disk, with appended "| start | end" fields.
     */
    @Override
    public String toFileString() {
        return super.toFileString() + " | " + start.toFileString() + " | " + end.toFileString() + tagsFileSuffix();
    }

    /**
     * Returns a human-readable representation including the start and
     * end times and any tags, e.g. "[E][ ] meeting (from: ... to: ...) #fun".
     */
    @Override
    public String toString() {
        return super.toString() + " (from: " + start.toDisplayString() + " to: " + end.toDisplayString() + ")"
                + tagsDisplaySuffix();
    }

    /**
     * Returns the parsed start time as a LocalDateTime, or null if the
     * original input couldn't be parsed as a date.
     */
    public LocalDateTime getStart() {
        return start.toDateTimeOrNull();
    }

    /**
     * Returns the parsed end time as a LocalDateTime, or null if the
     * original input couldn't be parsed as a date.
     */
    public LocalDateTime getEnd() {
        return end.toDateTimeOrNull();
    }

    /**
     * Returns the calendar date this event starts on, or empty if the
     * original input couldn't be parsed as a date.
     */
    @Override
    public Optional<LocalDate> getDate() {
        return Optional.ofNullable(getStart()).map(LocalDateTime::toLocalDate);
    }

    /**
     * Returns this task's duplicate-detection signature, including its
     * start/end fields alongside the base type/name signature.
     */
    @Override
    String duplicateSignature() {
        return super.duplicateSignature() + "|" + start.toFileString() + "|" + end.toFileString();
    }
}
