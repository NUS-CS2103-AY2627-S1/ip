package tasktracker.exception;

/**
 * Represents errors specific to the TaskTracker application.
 */
public class TaskTrackerException extends Exception {

    /**
     * Constructs a new TaskTrackerException with a detailed message.
     *
     * @param message The error message to display to the user.
     */
    public TaskTrackerException(String message) {
        super(message);
    }
}
