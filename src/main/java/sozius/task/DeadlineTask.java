package sozius.task;

/**
 * DeadlineTask class represents deadline tasks
 */
public class DeadlineTask extends Task {
    private final DueDate deadline;

    /**
     * Creates a DeadlineTask.
     * @param description task description
     * @param deadline task deadline
     */
    public DeadlineTask(String description, DueDate deadline) {
        super(description);
        this.deadline = deadline;
    }

    /**
     * Returns string representation of the task.
     * For storing to a file.
     */
    @Override
    public String toFileString() {
        return String.format("D | %c | %s | %s",
                super.isDone() ? '1' : '0',
                super.getDescription(),
                deadline.toFileString());
    }

    /**
     * Returns string representation of the task.
     * For displaying to user.
     */
    @Override
    public String toUserString() {
        return String.format("[D]%s (by: %s)",
                super.toUserString(),
                deadline.toUserString());
    }
}
