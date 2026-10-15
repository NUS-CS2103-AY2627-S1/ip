package bingusdingus.task;

/** Represents a task without a deadline or scheduled time. */
public class Todo extends Task {

    /** Creates an incomplete todo task. */
    public Todo(String description) {
        super(description);
    }

    /** Returns the todo task's display text. */
    @Override
    public String toString() {
        return "[T][" + this.getStatusIcon() + "] " + this.getDescription();
    }
}
