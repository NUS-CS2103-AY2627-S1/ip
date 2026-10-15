package kiwi.task;

/**
 * A to-do task with no date or time attached.
 */
public class Todo extends Task {
    /**
     * Creates a to-do from its description.
     *
     * @param description what needs to be done.
     */
    public Todo(String description) {
        super(description, TaskType.TODO);
    }
}
