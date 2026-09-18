package chud.task;

/** A task with a description only -- no date or time attached. */
public class Todo extends Task {
    /** Creates a new, not-yet-done todo with the given description. */
    public Todo(String description) {
        super(description);
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.TODO;
    }
}
