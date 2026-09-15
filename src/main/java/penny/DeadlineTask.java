package penny;

import java.time.LocalDateTime;

/**
 * DeadlineTask with a description and a deadline date.
 */
public class DeadlineTask extends Task {
    private static final String BY_KEYWORD = "/by";

    protected LocalDateTime deadline;

    protected DeadlineTask(String description, LocalDateTime deadline) {
        super(description);
        this.deadline = deadline;
        this.icon = "D";
    }

    /**
     * Returns DeadlineTask.
     *
     * @param arguments description /by deadline of deadline task.
     * @return DeadlineTask.
     * @throws PennyException if /by is missing, or description or deadline is empty.
     */
    public static DeadlineTask create(String arguments) throws PennyException {
        String[] parts = TaskArguments.split(arguments, BY_KEYWORD);
        return new DeadlineTask(parts[0], DateTime.parse(parts[1]));
    }

    @Override
    public boolean isDueOn(LocalDateTime dateTime) {
        return this.deadline.toLocalDate().equals(dateTime.toLocalDate());
    }

    @Override
    public String toString() {
        return super.toString()
                + " (by: " + DateTime.format(this.deadline) + ")";
    }
}
