package duke;

/** A task without an attached date or time. */
public class Todo extends Task {

    /** Creates a not-done todo task. */
    public Todo(String description) {
        super(description, TaskType.TODO);
    }
}
