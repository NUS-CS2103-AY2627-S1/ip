package luna.task;

import luna.exception.LunaException;

/**
 * Represents a task with a description and completion status.
 */
public abstract class Task {
    private final String description;
    private boolean isDone;

    /**
     * Creates a task with the given description.
     *
     * @param description Task description.
     */
    protected Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the description of the task.
     *
     * @return Task description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Marks the task as done.
     */
    public void markDone() {
        isDone = true;
    }

    /**
     * Marks the task as not done.
     */
    public void markUndone() {
        isDone = false;
    }

    /**
     * Returns whether the task is done.
     *
     * @return {@code true} if done, otherwise {@code false}.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns a status icon for the task.
     *
     * @return {@code "X"} if done, otherwise {@code " "}.
     */
    private String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", this.getStatusIcon(), this.description);
    }

    /**
     * Returns the file format representation of this task.
     *
     * @return One-line string for saving in the data file.
     */
    public abstract String toFileString();

    /**
     * Parses a saved line and creates the corresponding {@code Task} object.
     *
     * @param line Line from the data file.
     * @return Parsed task.
     * @throws LunaException If the line format is invalid.
     */
    public static Task fromFileLine(String line) throws LunaException {
        String[] parts = line.split("\\s*\\|\\s*");
        if (parts.length < 3) {
            throw new LunaException("Invalid line in data file: " + line);
        }

        String type = parts[0];
        switch (type) {
            case "T":
                return Todo.fromFileParts(parts);
            case "D":
                return Deadline.fromFileParts(parts);
            case "E":
                return Event.fromFileParts(parts);
            default:
                throw new LunaException("Unknown task type in data file: " + type);
        }
    }

    /**
     * Converts a done flag read from file into a boolean value,
     * indicating whether a task is marked as done.
     *
     * @param doneFlag Done flag from file (expected {@code "0"} or {@code "1"}).
     * @return {@code true} if the flag is {@code "1"}, otherwise {@code false}.
     * @throws LunaException If the done flag is invalid.
     */
    protected static boolean parseDoneFlag(String doneFlag) throws LunaException {
        switch (doneFlag) {
            case "0":
                return false;
            case "1":
                return true;
            default:
                throw new LunaException("Invalid completion flag in data file: " + doneFlag);
        }
    }

    /**
     * Formats the done status as the string flag used in the data file.
     *
     * @param isDone Whether the task is done.
     * @return {@code "1"} if done, otherwise {@code "0"}.
     */
    protected static String formatDoneFlag(boolean isDone) {
        return isDone ? "1" : "0";
    }
}
