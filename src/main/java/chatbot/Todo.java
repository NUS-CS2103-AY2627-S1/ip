package chatbot;

/**
 * Represents a task without a deadline or event period.
 */
public class Todo extends Task {

    /**
     * Initialises a new Todo with the given input as the task description.
     *
     * @param input Task description.
     */
    public Todo(String input) {
        super(input);
    }

    /**
     * Checks if a given event is a duplicate of current object.
     *
     * @param t Todo to be checked against current instance.
     * @return True if input todo has same description as current object, False otherwise.
     */
    public boolean isDuplicate(Todo t) {
        return super.hasSameDescription(t.getDescription());
    }

    /**
     * Returns the todo's type marker, completion status, and description.
     *
     * @return Formatted todo description.
     */
    @Override
    public String toString() {
        return "[T] " + this.getCompletionStatus() + " " + this.getDescription();
    }
}
