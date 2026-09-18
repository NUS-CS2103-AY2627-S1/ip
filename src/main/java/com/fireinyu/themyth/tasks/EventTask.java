package com.fireinyu.themyth.tasks;

import java.util.List;

import com.fireinyu.themyth.util.MythDateTime;

/**
 * Task occurring between start and end datetimes.
 *
 * @see MythDateTime
 */
public class EventTask extends Task {
    private MythDateTime from;
    private MythDateTime to;

    /**
     * Initializes an EventTask occurring between start and end datetimes.
     *
     * @param description description of the task.
     * @param from start datetime of the task.
     * @param to end datetime of the task.
     * @see MythDateTime
     * @see String
     */
    public EventTask(String description, MythDateTime from, MythDateTime to) {
        super(description, "E");
        this.from = from;
        this.to = to;
    }

    /**
     * Serializes this EventTask into a List of String attributes.
     *
     * @return List of String attributes representing the serialized EventTask object.
     * @see List
     * @see String
     */
    @Override
    public List<String> extract() {
        List<String> extracted = super.extract();
        extracted.addAll(List.of(
                from.dump(),
                to.dump()
        ));
        return extracted;
    }

    /**
     * Checks if this event is ongoing at the given date/time.
     *
     * @param dateTime The date/time to check.
     * @return true if the event contains the given date/time, false otherwise.
     */
    public boolean contains(MythDateTime dateTime) {
        return dateTime.isBetween(from, to);
    }

    /**
     * Returns a detailed String representation of this Task.
     * Includes its type, whether it is completed, its description, start and end dates
     *
     * @return detailed String representation of this Task.
     */
    @Override
    public String toString() {
        return String.format("%s (from: %s to: %s)", super.toString(), from, to);
    }
}
