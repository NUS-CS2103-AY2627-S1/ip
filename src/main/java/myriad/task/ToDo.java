package myriad.task;

/**
 * A task with a description and nothing else — no date of its own, so it
 * never matches the {@code show} or {@code stats} commands
 * ({@link Task#overlaps} stays false).
 */
public class ToDo extends Task {

    /**
     * Creates a not-done {@code ToDo} with the given description.
     *
     * @param description what the user typed after {@code todo}.
     */
    public ToDo(String description) {
        super(description);
    }

    @Override
    public TaskType getType() {
        return TaskType.TODO;
    }

    /**
     * Returns this task's save-format line prefixed with its type code so it can
     * be recognized as a {@code ToDo} when the data file is read back in.
     */
    @Override
    public String toSaveFormat() {
        return String.format("%s | %s", getType().getCode(), super.toSaveFormat());
    }

    /**
     * Returns the display form, e.g. {@code [T][ ] read book}: the
     * {@code [T]} tag marks it as a {@code ToDo}, and the rest is
     * {@link Task}'s own status-and-description form.
     */
    @Override
    public String toString() {
        return String.format("[T]%s", super.toString());
    }
}
