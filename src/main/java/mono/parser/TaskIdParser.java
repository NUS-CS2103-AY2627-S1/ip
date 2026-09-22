package mono.parser;

import mono.exception.WrongFormatException;

/**
 * Parses task IDs supplied to task-management tools.
 */
public final class TaskIdParser {
    /** Prevents instantiation of this utility class. */
    private TaskIdParser() {
    }

    /**
     * Parses a positive integer task ID.
     *
     * @param arguments tool arguments containing the task ID
     * @return positive task ID
     * @throws WrongFormatException if the task ID is empty, non-numeric, or non-positive
     */
    public static int parse(String arguments) throws WrongFormatException {
        if (arguments == null) {
            throw new WrongFormatException("Task ID is required.");
        }
        String taskId = arguments.trim();

        if (taskId.isEmpty()) {
            throw new WrongFormatException("Task ID is required.");
        }

        try {
            int id = Integer.parseInt(taskId);
            if (id <= 0) {
                throw new WrongFormatException(
                        "Task ID must be a positive integer.");
            }
            return id;
        } catch (NumberFormatException e) {
            throw new WrongFormatException(
                    "Task ID must be a positive integer.", e);
        }
    }
}
