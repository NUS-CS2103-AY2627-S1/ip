package ekud.task;

/**
 * A task with no date or time attached, for example {@code visit new theme park}.
 */
public class Todo extends Task {
    /**
     * Creates a todo with the given description.
     *
     * @param description text describing the todo
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns {@code T}, the type icon for todos.
     *
     * @return {@code T}
     */
    @Override
    public String getTypeIcon() {
        return "T";
    }

    /**
     * Returns {@code todo} followed by the description.
     *
     * @return the add-command text for this todo
     */
    @Override
    public String toCommandString() {
        return "todo " + getDescription();
    }
}
