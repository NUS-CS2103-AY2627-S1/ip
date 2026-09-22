package stewie.model;

/**
 * Represents a task with a normalized description and completion status.
 */
public abstract class Task {
    private boolean isDone;
    private final String description;

    /**
     * Creates an unfinished task with surrounding whitespace removed and repeated whitespace collapsed.
     *
     * @param description Nonempty task description without storage separators or control characters.
     * @throws IllegalArgumentException If the description is null, blank, or contains a pipe or control character.
     */
    public Task(String description) {
        validateArgument(description, "Description");
        assert description != null && !description.isBlank()
                : "A task must have a non-blank description after validation";
        if (description.indexOf('|') >= 0 || description.chars().anyMatch(Character::isISOControl)) {
            throw new IllegalArgumentException("Descriptions cannot contain | or control characters.");
        }
        this.description = description.strip().replaceAll("(?U)\\s+", " ");
        this.isDone = false;
    }

    /**
     * Returns the completion marker used when displaying the task.
     *
     * @return {@code "X"} if the task is done, or a single space otherwise.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /**
     * Returns the description of the task.
     *
     * @return Normalized task description.
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Checks whether the task is complete.
     *
     * @return True if the task is done, or false otherwise.
     */
    public boolean isDone() {
        return this.isDone;
    }

    /**
     * Marks the task as done.
     */
    public void markAsDone() {
        this.isDone = true;
    }

    /**
     * Marks the task as not done.
     */
    public void markAsUndone() {
        this.isDone = false;
    }

    /**
     * Checks task identity using type, description, and dates, regardless of completion status.
     * Description casing and repeated whitespace do not create a distinct task.
     *
     * @param other Task to compare with.
     * @return True when both tasks represent the same details.
     */
    public boolean hasSameDetails(Task other) {
        if (other == null || getClass() != other.getClass()
                || !description.equalsIgnoreCase(other.description)) {
            return false;
        }
        if (this instanceof Deadline deadline && other instanceof Deadline otherDeadline) {
            return deadline.getDeadline().equals(otherDeadline.getDeadline());
        }
        if (this instanceof Event event && other instanceof Event otherEvent) {
            return event.getFrom().equals(otherEvent.getFrom()) && event.getTo().equals(otherEvent.getTo());
        }
        return true;
    }

    /**
     * Validates that an argument contains non-whitespace text.
     *
     * @param input Text to validate.
     * @param type Argument label used in the error message.
     * @throws IllegalArgumentException If the argument is null or blank.
     */
    protected static void validateArgument(String input, String type) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException(type + " cannot be empty.");
        }
    }
}
