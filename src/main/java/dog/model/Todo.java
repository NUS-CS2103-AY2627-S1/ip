package dog.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dog.exceptions.DogException;

/**
 * Represents a todo task that needs to be done without a specific deadline.
 */
public class Todo extends Task {
    static final String TASK_ICON = "T";

    /**
     * Creates a todo task with the specified description.
     *
     * @param description the task description.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Creates a todo task with the specified description and completion status.
     *
     * @param description the task description.
     * @param isDone      true if the task is completed, false otherwise.
     */
    public Todo(String description, boolean isDone) {
        super(description, isDone);
    }

    /**
     * Parses a todo task from its saved string format.
     *
     * @param saveString the saved string in format "T | [X or space] | description".
     * @return the parsed Todo, or null if the format is invalid.
     */
    public static Todo fromSaveFormat(String saveString) {
        if (saveString == null) {
            return null;
        }

        String[] parts = saveString.split(" \\| ", 3);
        if (parts.length < 3) {
            return null;
        }

        String taskIcon = parts[0];
        String statusIcon = parts[1];
        String description = parts[2];

        if (!taskIcon.equals(TASK_ICON)) {
            return null;
        }
        if (isInvalidStatusIcon(statusIcon)) {
            return null;
        }

        boolean isDone = isCompletedStatusIcon(statusIcon);

        return new Todo(description, isDone);
    }

    /**
     * Parses a todo task from user input.
     *
     * @param input the user input string.
     * @return the parsed Todo.
     * @throws DogException if the input format is invalid.
     */
    public static Todo parse(String input) throws DogException {
        final String badInputMessage = "ARF ARF ToDo tasks must have a description!\n"
                + "Expected: todo <description>";

        // Pattern: "<description>"
        Pattern pattern = Pattern.compile("^\\s*(.+?)\\s*$");
        Matcher matcher = pattern.matcher(input);

        if (!matcher.matches()) {
            throw new DogException(badInputMessage);
        }

        assert matcher.groupCount() == 1 : "Todo regex group count should be 1";
        String description = matcher.group(1).trim();

        if (description.isEmpty()) {
            throw new DogException(badInputMessage);
        }

        return new Todo(description);
    }

    /**
     * Returns a string representation of the todo task for saving to file.
     *
     * @return the todo in save format.
     */
    @Override
    public String toSaveFormat() {
        return String.format(TASK_ICON + " | %s | %s", getStatusIcon(), description);
    }

    /**
     * Returns a string representation of the todo task for display.
     *
     * @return the todo in display format.
     */
    @Override
    public String toString() {
        return "[" + TASK_ICON + "]" + super.toString();
    }
}
