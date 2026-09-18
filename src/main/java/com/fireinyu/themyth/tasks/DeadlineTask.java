package com.fireinyu.themyth.tasks;

import java.util.List;

import com.fireinyu.themyth.util.MythDateTime;

/**
 * Task due by a certain datetime.
 *
 * @see MythDateTime
 */
public class DeadlineTask extends Task {
    private final MythDateTime date;

    /**
     * Initializes a DeadLineTask due by a certain datetime.
     *
     * @param description description of the task.
     * @param date due date of the task.
     * @see MythDateTime
     * @see String
     */
    public DeadlineTask(String description, MythDateTime date) {
        super(description, "D");
        this.date = date;
    }

    /**
     * Serializes this DeadlineTask into a List of String attributes.
     *
     * @return List of String attributes representing the serialized DeadlineTask object.
     * @see List
     * @see String
     */
    @Override
    public List<String> extract() {
        List<String> extracted = super.extract();
        extracted.addAll(List.of(
                date.dump()
        ));
        return extracted;
    }

    /**
     * Returns whether this DeadlineTask is due by a given datetime.
     *
     * @param dateTime the given datetime.
     * @return whether this DeadlineTask is due by dateTime.
     * @see MythDateTime
     */
    public boolean isDueBy(MythDateTime dateTime) {
        return date.isBefore(dateTime);
    }

    /**
     * Returns a detailed String representation of this Task.
     * Includes its type, whether it is completed, its description and due date
     *
     * @return detailed String representation of this Task.
     */
    @Override
    public String toString() {
        return String.format("%s (by: %s)", super.toString(), date);
    }
}
