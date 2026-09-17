package botavius.tasklist;

import java.time.LocalDateTime;

/** A task that includes a textual deadline. */
public class Deadline extends Task {

    /** Text describing when the task is due. */
    private LocalDateTime by;

    /**
     * Creates an incomplete deadline task.
     *
     * @param description task description
     * @param by deadline text, such as {@code Sunday}
     * @throws BotaviusException if the deadline is missing
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = TaskDateTime.parse(by, "deadline");
    }

    /** Returns the deadline date and time.
     *
     * @return the deadline
     */
    public LocalDateTime getBy() {
        return by;
    }

    /**
     * Formats the deadline task for display.
     *
     * @return task text with its deadline
     */
    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: "
                + TaskDateTime.toDisplayString(by) + ")";
    }

    /**
     * Returns the task in the format used when persisting it.
     *
     * @return the deadline task's storage representation
     */
    public String toStorageString() {
        return "[D]" + super.toStorageString() + " by: "
                + TaskDateTime.toStorageString(by);
    }
}
