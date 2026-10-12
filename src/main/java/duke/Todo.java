package duke;

/**
 * Represents a todo task, which has only a description.
 */
public class Todo extends Task {
    /**
     * Creates a todo with the given description.
     *
     * @param name the task description
     */
    public Todo(String name) {
        super(name);
    }

    /**
     * Creates a Todo from the user's argument.
     *
     * @param info the description typed by the user
     * @return the created Todo
     * @throws DuckeException if the description is empty
     */
    public static Todo of(String info) throws DuckeException {
        if (info.isBlank()) {
            throw new DuckeException("The description of a todo cannot be empty");
        }
        return new Todo(info.trim());
    }

    @Override
    public String getTypeIcon() {
        return "T";
    }
}
