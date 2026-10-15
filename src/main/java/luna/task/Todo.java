package luna.task;

import luna.exception.LunaException;

/**
 * Represents a {@code Task} with no additional time-related information.
 */
public class Todo extends Task {

    /**
     * Creates a to-do task.
     *
     * @param description Task description.
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    @Override
    public String toFileString() {
        return "T | " + formatDoneFlag(isDone()) + " | " + getDescription();
    }

    /**
     * Creates a {@link Todo} from saved parts of a file line.
     *
     * @param parts Split components of a saved line.
     * @return Parsed to-do.
     * @throws LunaException If the line format is invalid.
     */
    protected static Todo fromFileParts(String[] parts) throws LunaException {
        if (parts.length != 3) {
            throw new LunaException("Invalid todo line in data file");
        }

        Todo todo = new Todo(parts[2]);
        if (parseDoneFlag(parts[1])) {
            todo.markDone();
        }
        return todo;
    }
}
