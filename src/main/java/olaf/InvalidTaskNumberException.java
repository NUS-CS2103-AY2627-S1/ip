package olaf;

/**
 * Signals that a command refers to a task number that does not exist.
 */
final class InvalidTaskNumberException extends Exception {
    private static final long serialVersionUID = 1L;

    InvalidTaskNumberException(int taskCount) {
        super(createMessage(taskCount));
    }

    private static String createMessage(int taskCount) {
        if (taskCount == 0) {
            return "There are no tasks in your list.";
        }
        return "Task number must be between 1 and " + taskCount + ".";
    }
}
