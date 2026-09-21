package thaisbot.task;

/**
 * A simple to-do task without associated dates.
 */
public class Todo extends Task {
    /**
     * Create a Todo with the given description.
     * @param description task description
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    @Override
    public String toFileString() {
        String doneFlag = isDone() ? "1" : "0";
        return "T | " + doneFlag + " | " + getDescription() + formatTagsForStorage();
    }
}
