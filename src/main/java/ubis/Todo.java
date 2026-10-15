package ubis;

/**
 * Represents a todo task without any date or time constraints.
 */
public class Todo extends Task {

    /**
     * Initializes the todo task with the given description.
     *
     * @param input Task description.
     * @return Initialized Todo task, or null if description is invalid.
     */
    @Override
    public Task initialise(String input) {
        if (input == null || input.isBlank()) {
            setInitialisationError("Please provide a name for the todo.\n"
                    + "Example: todo read a book");
        } else {
            this.name = input.trim();
            this.type = TaskType.TODO;
            return this;
        }
        return null;
    }
}
