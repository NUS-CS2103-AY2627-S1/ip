package bro.task;

/** Represents a task without a deadline or event time. */
public class ToDos extends Task {
    /** Creates a todo task with the supplied description. */
    public ToDos(String description) {
        super(description, TaskType.TODO);
    }
}
