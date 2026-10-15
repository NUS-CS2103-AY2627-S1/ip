package pulbot.task;

/** Represents a task without a date or time constraint. */
public class Todo extends Task {
    public Todo(String description) {
        super(description, TaskType.TODO);
    }
}
