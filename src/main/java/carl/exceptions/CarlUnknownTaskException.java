package carl.exceptions;

/**
 * Represents an exception thrown when the user attempts to access, modify, or delete
 * a task index that does not exist in the task list.
 */
public class CarlUnknownTaskException extends CarlException {

    /**
     * Constructs a new {@code CarlUnknownTaskException} with a default error message
     * prompting the user to check the valid list of tasks.
     */
    public CarlUnknownTaskException() {
        super("That task number does not exist. Use `list` to check task numbers.");
    }
}
