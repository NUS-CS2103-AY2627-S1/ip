package wobble.tasks;

/** A task without a date or time attached to it. */
public class Todo extends Task {
    /** Creates an unfinished ToDo task. */
    public Todo(String description) {
        super(description, TaskType.TODO);
    }

    /** Returns this ToDo's display representation. */
    @Override
    public String toString() {
        return super.toString();
    }
}
