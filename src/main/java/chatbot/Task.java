package chatbot;

/**
 * Represents a task with a description and completion status.
 */
public class Task {
    private static final String COMPLETE_INDICATOR = "[X]";
    private static final String INCOMPLETE_INDICATOR = "[ ]";

    private final String description;
    private boolean isCompleted;

    /**
     * Initialises a new Task object with the given task description
     * @param description Task description.
     */
    public Task(String description) {
        if (description == null || description.isBlank()) {
            throw new InvalidInputException("Eh bro your task description cannot be empty.");
        }
        this.description = description.trim();
    }

    /**
     * Marks the task as complete.
     */
    public void markComplete() {
        isCompleted = true;
    }

    /**
     * Marks the task as incomplete.
     */
    public void markIncomplete() {
        isCompleted = false;
    }

    /**
     * Retrieve the task completion status.
     * @return A matching string to indicate if the task is completed or not.
     */
    public String getCompletionStatus() {
        if (isCompleted) {
            return COMPLETE_INDICATOR;
        }
        return INCOMPLETE_INDICATOR;
    }

    /**
     * Returns the task description.
     * @return Task description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Checks if the input task has the same description as the current object.
     *
     * @param input Description of the task to be checked against current instance's description.
     * @return True if input task has same description as current object, false otherwise.
     */
    public boolean hasSameDescription(String input) {
        return this.description.equals(input);
    }

    /**
     * Returns the task's completion marker and description.
     *
     * @return Formatted task description.
     */
    @Override
    public String toString() {
        return this.getCompletionStatus() + " " + this.description;
    }

}
