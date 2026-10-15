package koko;

import java.time.LocalDateTime;

/**
 * Represents the optional fields requested by an {@code update} command.
 */
public class UpdateRequest {
    private final int taskIndex;
    private final String description;
    private final LocalDateTime by;
    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Creates an update request for one task.
     * A {@code null} field means that its current value should be kept.
     *
     * @param taskIndex the zero-based index of the task to update
     * @param description the replacement description, if supplied
     * @param by the replacement deadline time, if supplied
     * @param from the replacement event start time, if supplied
     * @param to the replacement event end time, if supplied
     */
    public UpdateRequest(int taskIndex, String description, LocalDateTime by,
                         LocalDateTime from, LocalDateTime to) {
        this.taskIndex = taskIndex;
        this.description = description;
        this.by = by;
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the zero-based index of the task to update.
     *
     * @return the task index
     */
    public int getTaskIndex() {
        return taskIndex;
    }

    /**
     * Returns the replacement description, if supplied.
     *
     * @return the replacement description, or {@code null}
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the replacement deadline time, if supplied.
     *
     * @return the replacement deadline time, or {@code null}
     */
    public LocalDateTime getBy() {
        return by;
    }

    /**
     * Returns the replacement event start time, if supplied.
     *
     * @return the replacement event start time, or {@code null}
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns the replacement event end time, if supplied.
     *
     * @return the replacement event end time, or {@code null}
     */
    public LocalDateTime getTo() {
        return to;
    }

    /**
     * Checks whether this request includes a field that only deadlines use.
     *
     * @return true if a deadline time was supplied
     */
    public boolean hasBy() {
        return by != null;
    }

    /**
     * Checks whether this request includes a field that only events use.
     *
     * @return true if an event start time was supplied
     */
    public boolean hasFrom() {
        return from != null;
    }

    /**
     * Checks whether this request includes a field that only events use.
     *
     * @return true if an event end time was supplied
     */
    public boolean hasTo() {
        return to != null;
    }
}
