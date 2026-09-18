package toothless.task;

import java.time.LocalDate;

/**
 * Describes the task fields that should change during one edit operation.
 */
public class TaskUpdate {
    private final String description;
    private final LocalDate by;
    private final String from;
    private final String to;

    /**
     * Creates an update whose non-null values identify the fields to replace.
     *
     * @param description replacement task description, or {@code null} to keep it unchanged.
     * @param by replacement deadline date, or {@code null} to keep it unchanged.
     * @param from replacement event start, or {@code null} to keep it unchanged.
     * @param to replacement event end, or {@code null} to keep it unchanged.
     */
    public TaskUpdate(String description, LocalDate by, String from, String to) {
        this.description = description;
        this.by = by;
        this.from = from;
        this.to = to;
    }

    /**
     * Returns whether this update contains at least one replacement field.
     *
     * @return true when any supported field has a replacement value
     */
    public boolean hasAnyField() {
        return description != null || by != null || from != null || to != null;
    }

    /**
     * Returns whether this update replaces the deadline date.
     *
     * @return true when a replacement deadline date is present
     */
    public boolean hasBy() {
        return by != null;
    }

    /**
     * Returns whether this update replaces either event time.
     *
     * @return true when a replacement event start or end is present
     */
    public boolean hasEventTime() {
        return from != null || to != null;
    }

    /**
     * Returns the replacement description, or the existing value when omitted.
     *
     * @param existingDescription description currently stored by the task.
     * @return effective description after applying this update
     */
    public String getDescriptionOr(String existingDescription) {
        return description == null ? existingDescription : description;
    }

    /**
     * Returns the replacement deadline date, or the existing value when omitted.
     *
     * @param existingBy deadline date currently stored by the task.
     * @return effective deadline date after applying this update
     */
    public LocalDate getByOr(LocalDate existingBy) {
        return by == null ? existingBy : by;
    }

    /**
     * Returns the replacement event start, or the existing value when omitted.
     *
     * @param existingFrom event start currently stored by the task.
     * @return effective event start after applying this update
     */
    public String getFromOr(String existingFrom) {
        return from == null ? existingFrom : from;
    }

    /**
     * Returns the replacement event end, or the existing value when omitted.
     *
     * @param existingTo event end currently stored by the task.
     * @return effective event end after applying this update
     */
    public String getToOr(String existingTo) {
        return to == null ? existingTo : to;
    }
}
