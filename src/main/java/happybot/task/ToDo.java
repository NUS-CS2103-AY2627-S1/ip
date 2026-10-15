package happybot.task;

/**
 * Represents a to-do task managed by HappyBot.
 */
public class ToDo extends Task {

    /**
     * Creates a todo with the specified description.
     *
     * @param description The to-do description.
     */
    public ToDo(String description) {
        super(description);
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
