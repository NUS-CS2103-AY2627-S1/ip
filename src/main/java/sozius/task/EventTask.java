package sozius.task;

/**
 * Event task represents events.
 */
public class EventTask extends Task {
    private final DueDate from;
    private final DueDate to;

    /**
     * Creates an EventTask
     * @param desc task description
     * @param from start DueDate
     * @param to end DueDate
     */
    public EventTask(String desc, DueDate from, DueDate to) {
        super(desc);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns string representation of the task.
     * For storing to a file.
     */
    @Override
    public String toFileString() {
        return String.format("E | %c | %s | %s/%s",
                super.isDone() ? '1' : '0',
                super.getDescription(),
                from.toFileString(),
                to.toFileString());
    }

    /**
     * Returns string representation of the task.
     * For displaying to user.
     */
    @Override
    public String toUserString() {
        return String.format("[E]%s (from: %s to: %s)",
                super.toUserString(),
                from.toUserString(),
                to.toUserString());
    }
}
