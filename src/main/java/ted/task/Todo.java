package ted.task;

/**
 * A task with no date or time attached to it, e.g. {@code visit new theme park}.
 * The simplest kind of task: it adds nothing to {@link Task} beyond its type icon.
 */
public class Todo extends Task {
    /**
     * Creates a todo that is not done yet.
     *
     * @param description what the user wants to get done.
     */
    public Todo(String description) {
        super(description);
    }

    /** {@inheritDoc} A todo is shown as {@code T}. */
    @Override
    public String getTypeIcon() {
        return "T";
    }
}
