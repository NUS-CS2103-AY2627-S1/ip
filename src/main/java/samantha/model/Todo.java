package samantha.model;

import java.time.LocalDate;

import samantha.exception.TaskValidationException;

/**
 * Represents a task without a scheduled date.
 */
public class Todo extends Task {

    /**
     * Creates a todo task with the supplied description.
     *
     * @param name task description
     * @throws TaskValidationException if the description is blank
     */
    public Todo(String name) throws TaskValidationException {
        super(name, "todo");
    }

    /**
     * Returns the display marker for a todo task.
     *
     * @return the todo marker
     */
    @Override
    public String getType() {
        return "[T]";
    }

    /**
     * Returns {@code false} because todos do not have scheduled dates.
     *
     * @param date date to check
     * @return {@code false}
     */
    @Override
    public boolean isOnDate(LocalDate date) {
        return false;
    }

    /**
     * Returns this todo as one record for the task data file.
     *
     * @return serialized todo task
     */
    @Override
    public String toFileString() {
        return String.format("T | %d | %s", getCompletionStatusCode(), getTaskName());
    }
}
