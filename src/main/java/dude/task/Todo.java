package dude.task;

import dude.exception.UsageDetails;
import dude.exception.UsageException;

/**
 * A task without an attached date or time.
 */
public class Todo extends Task {
    /**
     * Creates an incomplete todo task with the supplied description.
     *
     * @param description Text describing the task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Creates a todo task from command input.
     *
     * @param input Raw text following the todo command.
     * @return Parsed todo task.
     * @throws UsageException If the input does not contain task details.
     */
    public static Todo fromInput(String input) throws UsageException {
        if (input == null || input.isBlank()) {
            throw usageError("<missing>", "non-empty text", "<task details>");
        }
        return new Todo(input.trim());
    }

    private static UsageException usageError(String actualValue, String expectedType,
            String usageToken) {
        return new UsageException(new UsageDetails("todo", "task details", actualValue,
                expectedType, "Usage: todo <task details>", usageToken));
    }

    /**
     * Returns the formatted todo representation.
     *
     * @return Todo type marker followed by the task status and description.
     */
    @Override
    public String toString() {
        return String.format("[T]%s", super.toString());
    }
}
