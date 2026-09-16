package miku.task;

/** A task that has no date or time attached to it. */
public class Todo extends Task {
    /** Creates an undone todo task. */
    public Todo(String description) {
        super(TaskType.TODO, description);
    }
}
