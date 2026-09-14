package stuart.task;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Represents a task that needs to be done before a specific date/time.
 */
// Deadlines class built by Claude Code, verified by myself
public class Deadlines extends Task {
    private final LocalDate by;

    /**
     * Creates a new deadline task.
     *
     * @param description what the deadline is
     * @param by the date the task is due
     */
    public Deadlines(String description, LocalDate by) {
        super(description, TaskType.DEADLINE);
        this.by = by;
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + by.format(DISPLAY_DATE_FORMAT) + ")";
    }

    @Override
    public String toSaveFormat() {
        return super.toSaveFormat() + SAVE_DELIMITER + by;
    }

    @Override
    public boolean occursOn(LocalDate date) {
        return by.equals(date);
    }

    @Override
    public Optional<LocalDate> getSortDate() {
        return Optional.of(by);
    }

    @Override
    public boolean isOverdue() {
        return !isDone && by.isBefore(LocalDate.now());
    }

    @Override
    public boolean isDueSoon(int days) {
        LocalDate today = LocalDate.now();
        return !isDone && !by.isBefore(today) && !by.isAfter(today.plusDays(days));
    }

    @Override
    public boolean hasSameDetails(Task other) {
        return super.hasSameDetails(other) && by.equals(((Deadlines) other).by);
    }
}
