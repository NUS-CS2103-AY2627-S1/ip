package org.cs2103t.marquee.cli.task;

import java.time.LocalDateTime;

import org.cs2103t.marquee.core.task.Task;
import org.cs2103t.marquee.core.time.DateTimeFormatter;

/**
 * Representation of an event with a concrete starting and ending time.
 *
 * @see Task
 */
public final class EventTask extends Task {
    /**
     * Creates a new {@code EventTask} with the given description,
     * starting time and ending time, then mark it as either completed or incomplete.
     *
     * @param description the description of the event
     * @param start when the event starts
     * @param end when the event ends
     * @param isMarked whether the event has been completed or not
     */
    public EventTask(String description, LocalDateTime start, LocalDateTime end, boolean isMarked)
            throws IllegalArgumentException {
        super(description, isMarked, start, end, TaskTags.EVENT_TAG);
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("End time must be after start time");
        }
    }

    /**
     * Creates a new {@code EventTask} with the given description,
     * starting time and ending time, then mark it as incomplete.
     *
     * @param description the description of the event
     * @param start when the event starts
     * @param end when the event ends
     */
    public EventTask(String description, LocalDateTime start, LocalDateTime end) throws IllegalArgumentException {
        this(description, start, end, false);
    }

    private EventTask() {
        super();
    }

    @Override
    public String toString() {
        return super.toString()
                + " (from " + DateTimeFormatter.formatDateTime(this.getStart())
                + " to " + DateTimeFormatter.formatDateTime(this.getEnd()) + ")";
    }
}
