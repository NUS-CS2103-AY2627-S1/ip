package suki;

/**
 * Represents a task without any date/time attached to it.
 *
 * <p>This is the simplest kind of task: it adds nothing to {@link Task} beyond
 * its own type letter.
 */
public class Todo extends Task {
    /**
     * Creates a todo that is not yet done.
     *
     * @param description what the user wants to do
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * {@inheritDoc}
     *
     * @return "T", marking this as a todo
     */
    @Override
    public String getTypeIcon() {
        return "T";
    }
}
