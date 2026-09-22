package wiz;

/**
 * Represents a Todo task without specific deadline or schedule.
 */
public class ToDo extends Task {

    /**
     * Constructs a ToDo task with the given description.
     *
     * @param description The task description.
     */
    public ToDo(String description) {
        super(description);
        assert this.description != null && !this.description.isBlank() : "ToDo description cannot be blank";
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    @Override
    public String toFileString() {
        return "T | "
                + (isDone ? "1" : "0")
                + " | "
                + description;
    }
}