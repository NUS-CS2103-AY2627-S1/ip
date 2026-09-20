package grower.exceptions;

/**
 * Represents an attempt to access a task number that is not in the task list.
 */
public class InvalidTaskNumberException extends GrowerException {
    /**
     * Creates an exception with a message describing the valid task numbers.
     *
     * @param index Zero-based index that was requested.
     * @param numberOfTasks Current number of tasks.
     */
    public InvalidTaskNumberException(int index, int numberOfTasks) {
        super(createMessage(index, numberOfTasks));
    }

    /**
     * Creates an error message that includes the valid one-based task-number range.
     *
     * @param index Zero-based index that was requested.
     * @param numberOfTasks Current number of tasks.
     * @return Message describing why the requested task number is invalid.
     */
    private static String createMessage(int index, int numberOfTasks) {
        if (numberOfTasks == 0) {
            return "There are no tasks in the list.";
        }

        return String.format(
                "Task number %d is invalid. Please choose a number from 1 to %d pls.",
                index + 1,
                numberOfTasks
        );
    }
}
