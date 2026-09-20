package loki.model;

import loki.parser.TaskUpdate;

/**
 * A task that does not have a deadline or scheduled time.
 */
public class ToDo extends Task {
    /**
     * Creates a new unfinished to-do task.
     *
     * @param title the task description.
     */
    public ToDo(String title) {
        super(title);
    }

    /**
     * Creates a to-do task with the supplied completion status.
     *
     * @param title the task description
     * @param done whether the task has been completed
     */
    public ToDo(String title, boolean done) {
        super(title, done);
    }

    /**
     * Creates a to-do task from the numeric status used in storage.
     *
     * @param title the task description
     * @param done the completion status, which must be {@code 0} or {@code 1}
     */
    public ToDo(String title, int done) {
        super(title, done);
    }

    /**
     * Formats this task with the to-do task type marker.
     *
     * @return the formatted to-do task.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    /**
     * Converts this to-do task into the storage format.
     *
     * @return the serialized to-do task.
     */
    @Override
    public String saveString() {
        int status = isDone() ? 1 : 0;
        return String.format("T | %d | %s", status, getTitle());
    }

    /**
     * Creates an updated to-do while preserving its completion status.
     *
     * @param update the replacement fields.
     * @return the updated to-do.
     * @throws IllegalArgumentException if the update contains deadline or event fields.
     */
    @Override
    public Task updatedWith(TaskUpdate update) {
        if (update.getDeadline().isPresent() || update.getEventStart().isPresent()
                || update.getEventEnd().isPresent()) {
            throw new IllegalArgumentException("A to-do only supports title updates");
        }
        String title = update.getTitle().orElse(getTitle());
        return new ToDo(title, isDone());
    }
}
