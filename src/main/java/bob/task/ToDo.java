package bob.task;

import java.time.LocalDateTime;

import bob.exception.BobException;

/**
 * Represents a todo task without any date or time attached.
 */
public class ToDo extends Task {

    /**
     * Constructs a ToDo task with the specified description.
     *
     * @param name the description of the todo task
     */
    public ToDo(String name) {
        super(name);
    }

    /**
     * Returns the string representation of the todo task, including its status
     * and description.
     *
     * @return formatted string representation of this todo task
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    /**
     * Formats the todo task as a string suitable for persistent storage export.
     *
     * @return delimiter-separated string representation of this todo task
     */
    @Override
    public String export() {
        return String.format(
                "T%c%s%c%s",
                STORAGE_DELIMITER,
                this.getDoneCode(), STORAGE_DELIMITER,
                this.name);
    }

    /**
     * Creates an updated copy of this {@link ToDo} task with the specified new description.
     *
     * @param newName     the new description, or null if unchanged
     * @param newDeadline the new deadline date-time (must be null for ToDo)
     * @param newFrom     the new start date-time (must be null for ToDo)
     * @param newTo       the new end date-time (must be null for ToDo)
     * @return a new updated {@link ToDo} instance with completion status preserved
     * @throws BobException if date flags (/by, /from, /to) are provided
     */
    @Override
    public Task update(String newName, LocalDateTime newDeadline,
            LocalDateTime newFrom, LocalDateTime newTo) throws BobException {
        if (newDeadline != null) {
            throw new BobException("Error: ToDo tasks do not have a deadline (/by)");
        }
        if (newFrom != null || newTo != null) {
            throw new BobException("Error: ToDo tasks do not have event dates (/from, /to)");
        }

        String targetName = newName != null ? newName : this.name;
        ToDo updated = new ToDo(targetName);
        if (this.isDone) {
            updated.mark();
        }
        return updated;
    }
}
