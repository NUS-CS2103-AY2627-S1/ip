package prism.task;

/** Represents a task with no associated date or time. */
public class Todo extends Task {

    /** Creates a todo with the supplied description. */
    public Todo(String description) {
        super(description, TaskType.TODO);
    }
}
