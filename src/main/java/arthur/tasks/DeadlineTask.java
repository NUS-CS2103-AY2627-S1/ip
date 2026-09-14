package arthur.tasks;

import java.time.LocalDate;
import java.time.LocalDateTime;

import arthur.utils.Utils;

/**
 * Represents a task that must be completed by a date.
 */
public class DeadlineTask extends Task {
    // AI-assisted: Named the deadline display format.
    private static final TaskType TASK_TYPE = TaskType.DEADLINE;
    private static final String TASK_DISPLAY_FORMAT = "%s%s %s (by %s)";
    // AI-assisted: Added the deadline task type with a by date.
    private final LocalDateTime by;

    /**
     * Creates a new deadline task.
     *
     * @param description The description of the deadline task.
     * @param by The deadline date of the deadline task.
     */
    public DeadlineTask(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }

    @Override
    // AI-assisted: Matched deadline tasks by their deadline date.
    public boolean isOn(LocalDate date) {
        return by.toLocalDate().equals(date);
    }

    @Override
    public String toString() {
        return String.format(TASK_DISPLAY_FORMAT, TASK_TYPE, getMarkedIcon(), getDescription(),
                Utils.formatDateTime(by));
    }
}
