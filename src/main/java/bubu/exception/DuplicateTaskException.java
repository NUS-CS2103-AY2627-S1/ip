package bubu.exception;

/** Indicates that an equivalent task already exists in the task list. */
public class DuplicateTaskException extends BubuException {
    private static final String DUPLICATE_TASK_MESSAGE =
            "Meow! An identical task already exists in your list.";

    /** Creates an error for a task that duplicates an existing task. */
    public DuplicateTaskException() {
        super(DUPLICATE_TASK_MESSAGE);
    }
}
