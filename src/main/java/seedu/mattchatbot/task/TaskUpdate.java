package seedu.mattchatbot.task;

import java.time.LocalDateTime;

/**
 * The parts of a task that an update command asks to change.
 * <p>
 * A null field means "leave this part alone", which is what lets a user
 * change an event's end time without restating its start. Every date here has
 * already been parsed, so applying an update can fail only because the task's
 * type has no such part -- never because a value turned out to be unreadable
 * halfway through. That is what keeps an update all-or-nothing.
 *
 * @param description the new description, or null to leave it unchanged
 * @param by          the new due date, or null to leave it unchanged
 * @param from        the new start time, or null to leave it unchanged
 * @param to          the new end time, or null to leave it unchanged
 */
public record TaskUpdate(String description, LocalDateTime by, LocalDateTime from,
        LocalDateTime to) {

    /**
     * Returns whether a new description was given.
     *
     * @return true if the description should change
     */
    public boolean hasDescription() {
        return description != null;
    }

    /**
     * Returns whether a new due date was given.
     *
     * @return true if the due date should change
     */
    public boolean hasBy() {
        return by != null;
    }

    /**
     * Returns whether a new start time was given.
     *
     * @return true if the start time should change
     */
    public boolean hasFrom() {
        return from != null;
    }

    /**
     * Returns whether a new end time was given.
     *
     * @return true if the end time should change
     */
    public boolean hasTo() {
        return to != null;
    }

    /**
     * Returns whether this update asks for nothing at all.
     *
     * @return true if no part was given
     */
    public boolean isEmpty() {
        return !hasDescription() && !hasBy() && !hasFrom() && !hasTo();
    }
}
