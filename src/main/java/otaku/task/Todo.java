package otaku.task;

/** Represents a task without a date or time. */
public class Todo extends Task {
    /** Creates an incomplete to-do task. */
    public Todo(String description) {
        super(description);
    }

    @Override
    protected String getTypeIcon() {
        return "T";
    }
}
