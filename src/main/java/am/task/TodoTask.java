package am.task;

import am.storage.CorruptedDataException;

/**
 * Represents a task without a deadline or event time.
 */
public class TodoTask extends Task {
    /**
     * Creates an incomplete todo task.
     *
     * @param name task description
     */
    public TodoTask(String name) {
        super(name);
    }

    private TodoTask(String name, boolean done) {
        super(name, done);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toSerialized() {
        return String.format("T | %s | %s", getSerializedStatus(), getName());
    }

    /**
     * Reconstructs a todo task from its persisted representation.
     *
     * @param line serialized todo task data
     * @return reconstructed todo task
     * @throws CorruptedDataException if the line has an invalid format
     */
    public static TodoTask fromSerialized(String line) throws CorruptedDataException {
        if (line == null || line.isBlank()) {
            throw new CorruptedDataException("Todo task data is empty");
        }

        String[] parts = line.trim().split("\\s*\\|\\s*", -1);
        if (parts.length != 3 || !parts[0].equals("T")) {
            throw new CorruptedDataException("Malformed todo task data: " + line);
        }

        boolean done = parseCompletionStatus(parts[1], "todo");
        return new TodoTask(parts[2], done);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("[T]%s", super.toString());
    }
}
