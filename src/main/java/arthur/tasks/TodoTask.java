package arthur.tasks;

import java.time.LocalDate;

/**
 * Represents a task without a date or time requirement.
 */
public class TodoTask extends Task {
    // AI-assisted: Named the to-do display format.
    private static final TaskType TASK_TYPE = TaskType.TODO;
    private static final String TASK_DISPLAY_FORMAT = "%s%s %s";

    // AI-assisted: Added the todo task type and its display format.
    public TodoTask(String description) {
        super(description);
    }

    @Override
    // AI-assisted: Excluded todos from date-based filtering.
    public boolean isOn(LocalDate date) {
        return false;
    }

    @Override
    public String toString() {
        return String.format(TASK_DISPLAY_FORMAT, TASK_TYPE, getMarkedIcon(), getDescription());
    }
}
