package benbot;

/** Indicates that the fixed-size in-memory task list cannot accept another task. */
public class TaskListFullException extends BenBotException {
    /** The version identifier used when this exception is serialized. */
    private static final long serialVersionUID = 1L;

    /** Creates an exception explaining that the task list has reached its limit. */
    public TaskListFullException() {
        super("Sorry, the task list is full.");
    }
}
