package neil.task;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import neil.exception.NeilException;

/**
 * Represents a task that can be displayed and stored by Neil.
 */
public abstract class Task {
    /** Description displayed for this task. */
    protected String description;
    /** Whether this task has been completed. */
    protected boolean isDone;

    /**
     * Creates an incomplete task with the specified description.
     *
     * @param description description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns a representation of this task suitable for persistent storage.
     *
     * @return encoded task data.
     */
    public abstract String encode();

    @Override
    public String toString() {
        return String.format("[%s] %s", isDone ? "X" : " ", description);
    }

    /**
     * Marks this task as completed.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks this task as incomplete.
     */
    public void unmark() {
        isDone = false;
    }

    /**
     * Returns whether this task's description contains the specified keyword.
     *
     * @param keyword keyword to find in the description.
     * @return true if the description contains the keyword, ignoring letter case.
     */
    public boolean hasKeyword(String keyword) {
        return description.toLowerCase(Locale.ROOT)
                .contains(keyword.toLowerCase(Locale.ROOT));
    }

    /**
     * Reconstructs a task from its stored representation.
     *
     * @param line encoded task data.
     * @return reconstructed task.
     * @throws NeilException if the stored data is invalid.
     */
    public static Task decode(String line) throws NeilException {
        String[] parts = line.split("\\s*\\|\\s*", -1);

        if (parts.length < 3) {
            throw new NeilException("Invalid saved task: " + line);
        }

        String type = parts[0];
        String status = parts[1];
        String description = parts[2];

        if (description.isBlank()) {
            throw new NeilException("Saved task has no description: " + line);
        }

        boolean isDone = parseDoneStatus(status, line);
        Task task = decodeTaskByType(type, description, parts, line);

        if (isDone) {
            task.markAsDone();
        }

        return task;
    }

    private static boolean parseDoneStatus(String status, String line) throws NeilException {
        if (status.equals("1")) {
            return true;
        }

        if (status.equals("0")) {
            return false;
        }

        throw new NeilException("Invalid saved task status: " + line);
    }

    private static Task decodeTaskByType(String type, String description, String[] parts, String line)
            throws NeilException {
        switch (type) {
            case "T":
                return decodeTodoTask(description, parts, line);
            case "D":
                return decodeDeadlineTask(description, parts, line);
            case "E":
                return decodeEventTask(description, parts, line);
            default:
                throw new NeilException("Unknown saved task type: " + type);
        }
    }

    private static Task decodeTodoTask(String description, String[] parts, String line) throws NeilException {
        if (parts.length != 3) {
            throw new NeilException("Invalid saved todo: " + line);
        }

        return new ToDoTask(description);
    }

    private static Task decodeDeadlineTask(String description, String[] parts, String line) throws NeilException {
        if (parts.length != 4 || parts[3].isBlank()) {
            throw new NeilException("Invalid saved deadline: " + line);
        }

        try {
            LocalDate deadline = LocalDate.parse(parts[3]);
            return new DeadlineTask(description, deadline);
        } catch (DateTimeParseException e) {
            throw new NeilException("Invalid saved deadline date: " + line);
        }
    }

    private static Task decodeEventTask(String description, String[] parts, String line) throws NeilException {
        if (parts.length != 5
                || parts[3].isBlank()
                || parts[4].isBlank()) {
            throw new NeilException("Invalid saved event: " + line);
        }

        try {
            return new EventTask(
                    description,
                    parts[3],
                    parts[4]
            );
        } catch (DateTimeParseException e) {
            throw new NeilException("Invalid saved event date/time: " + line);
        } catch (IllegalArgumentException e) {
            throw new NeilException("Saved event start must be earlier than its end: " + line);
        }
    }
}
