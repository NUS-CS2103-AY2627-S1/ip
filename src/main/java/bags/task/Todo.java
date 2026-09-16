package bags.task;

import bags.exception.BagsException;

/**
 * Represents a to-do task.
 */
public class Todo extends Task {

    /**
     * Creates a to-do task.
     *
     * @param description text describing the task
     */
    public Todo(String description) {
        super(description, TaskType.TODO);
        assert description != null : "Description must not be null";
    }

    /**
     * Creates a to-do task from an add-task command string.
     *
     * @param command user input after {@code todo}
     * @return the created to-do task
     * @throws BagsException if the description is missing
     */
    public static Todo createTask(String command) throws BagsException {
        assert command != null : "Command string must not be null";
        String[] words = command.split(" ");
        if (words.length < 2) {
            throw new BagsException("Oops! Your task is missing a description. Add some details after todo.");
        }

        StringBuilder name = new StringBuilder();
        for (int i = 1; i < words.length; i++) {
            name.append(words[i]).append(" ");
        }

        String description = name.toString().trim();
        if (description.isEmpty()) {
            throw new BagsException("Hmm, I still need a task description. Add some details after todo.");
        }
        return new Todo(description);
    }

    /**
     * Returns the task in a user-readable format.
     *
     * @return the formatted todo task
     */
    @Override
    public String toString() {
        return "[T][" + super.getStatusIcon() + "] " + super.getDescription();
    }

    /**
     * Converts the task into the format used for saving.
     *
     * @return the task as a storage record
     */
    @Override
    public String parseEvent() {
        return "T | [" + getStatusIcon() + "] | " + description;
    }
}
