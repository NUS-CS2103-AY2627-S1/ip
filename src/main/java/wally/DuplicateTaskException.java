package wally;

/** Indicates that a task description is already present in the task list. */
public class DuplicateTaskException extends Exception {
    /** Creates an error explaining why the task cannot be added. */
    public DuplicateTaskException() {
        super("A task with this description already exists.");
    }
}
