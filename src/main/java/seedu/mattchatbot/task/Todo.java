package seedu.mattchatbot.task;

/**
 * A task with no date or time attached, e.g. {@code visit new theme park}.
 */
public class Todo extends Task {

    /** The marker shown in a todo's type box and written to the save file. */
    public static final String TYPE_ICON = "T";

    /**
     * Creates a todo.
     *
     * @param description what the user wants to do
     */
    public Todo(String description) {
        super(description);
    }

    @Override
    public String getTypeIcon() {
        return TYPE_ICON;
    }

    @Override
    public String getTypeName() {
        return "todo";
    }
}
