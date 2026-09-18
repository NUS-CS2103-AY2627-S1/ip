package blud.model;

import blud.exception.ToDoException;

/** Represents a task without a deadline or event period. */
public class ToDo extends Task {
    /** Creates a todo task from its parsed command parts. */
    public ToDo(String[] parts) {
        super(validateAndExtractDescription(parts));
    }

    /** Returns the todo task in display format. */
    @Override
    public String toString() {
        return String.format("[T]%s", super.toString());
    }

    /** Returns the todo task in storage format. */
    @Override
    public String toStorageString() {
        return String.format("T | %d | %s", isDone() ? 1 : 0, getTaskDescription());
    }

    /** Validates the command parts and returns the todo description. */
    private static String validateAndExtractDescription(String[] parts) {
        if (parts == null || parts.length == 0 || parts[0].length() < 6) {
            throw new ToDoException();
        }
        return parts[0].substring(5).strip();
    }
}
