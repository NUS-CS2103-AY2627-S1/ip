package zaru.command;

import java.util.Set;

import zaru.exception.ZaruException;
import zaru.task.Task;
import zaru.task.TaskList;

/** Defines the common contract and validation helpers for user commands. */
public abstract class Command {
    private static final int MAX_DESCRIPTION_LENGTH = 500;

    /**
     * Returns the command word represented by this command.
     *
     * @return Lower-case command word.
     */
    public abstract String getCommand();

    /**
     * Executes this command using the application task list.
     *
     * @param tasks Current task list.
     * @return The response message for this command.
     * @throws ZaruException If command arguments or task operations are invalid.
     */
    public abstract String execute(TaskList tasks) throws ZaruException;

    /**
     * Returns the slash-prefixed arguments accepted by this command.
     *
     * @return Accepted keyed argument names without their leading slash.
     */
    public Set<String> getAllowedKeyedArguments() {
        return Set.of();
    }

    /**
     * Returns whether this command accepts text after its command word.
     *
     * @return {@code true} if the command accepts a positional argument.
     */
    public boolean acceptsPositionalArgument() {
        return true;
    }

    /**
     * Parses and validates a user-provided task number.
     *
     * @param tasks Current task list.
     * @param numberText Task number entered by the user.
     * @return Valid one-based task number.
     * @throws ZaruException If the text is missing, is not a whole number, or is outside the task list.
     */
    protected static int parseTaskNumber(TaskList tasks, String numberText) throws ZaruException {
        validateNonEmpty(numberText, "Please provide a number.");

        if (!numberText.matches("[0-9]+")) {
            throw new ZaruException("Please provide one whole task number.");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(numberText);
        } catch (NumberFormatException e) {
            throw new ZaruException("Number %s must be a valid number.".formatted(numberText));
        }

        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new ZaruException("Task number must be between 1 and %d!".formatted(tasks.size()));
        }

        return taskNumber;
    }

    /**
     * Adds a task and creates the standard task-added response.
     *
     * @param tasks Current task list.
     * @param task Task to add.
     * @return Task-added response message.
     * @throws ZaruException If the updated task list cannot be saved.
     */
    protected static String addTaskAndCreateResponse(TaskList tasks, Task task) throws ZaruException {
        tasks.add(task);
        int numberOfTasks = tasks.size();

        return "Got it! I've tucked this task into your list:\n   %s\nYou now have %d task%s.".formatted(
                tasks.getTaskString(numberOfTasks),
                numberOfTasks,
                numberOfTasks == 1 ? "" : "s");
    }

    /**
     * Checks whether required command text is present.
     *
     * @param text Text to check.
     * @param errorMessage Message to show if the text is missing.
     * @throws ZaruException If the text is null or blank.
     */
    protected static void validateNonEmpty(String text, String errorMessage) throws ZaruException {
        if (text == null || text.isBlank()) {
            throw new ZaruException(errorMessage);
        }
    }

    /**
     * Checks whether a task description is safe to display and persist.
     *
     * @param description Task description to check.
     * @param emptyMessage Message to show if the description is empty.
     * @throws ZaruException If the description is empty, too long, or contains reserved characters.
     */
    protected static void validateDescription(String description, String emptyMessage) throws ZaruException {
        validateNonEmpty(description, emptyMessage);

        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new ZaruException("Task descriptions cannot exceed %d characters."
                    .formatted(MAX_DESCRIPTION_LENGTH));
        }
        if (description.contains("|")) {
            throw new ZaruException("Task descriptions cannot contain the reserved | character.");
        }
        if (description.chars().anyMatch(Character::isISOControl)) {
            throw new ZaruException("Task descriptions cannot contain line breaks or control characters.");
        }
    }

    /** Returns whether this command is an exit command. */
    public boolean isExit() {
        return false;
    }
}
