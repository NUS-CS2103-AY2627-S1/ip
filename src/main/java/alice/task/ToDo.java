package alice.task;

import java.time.LocalDateTime;

/**
 * Represents a task without a date or time.
 */
public class ToDo extends Task {
    /**
     * Creates a todo task.
     *
     * @param description The task description.
     */
    public ToDo(String description) {
        super(description);
    }

    /**
     * Rejects a recurrence because todos have no scheduled date.
     *
     * @param recurrence The requested recurrence interval.
     * @throws UnsupportedOperationException Always, because todos cannot recur.
     */
    @Override
    public void setRecurrence(Recurrence recurrence) {
        throw new UnsupportedOperationException("Todos cannot recur.");
    }

    /**
     * Rejects creating another occurrence because todos have no scheduled date.
     *
     * @throws UnsupportedOperationException Always, because todos cannot recur.
     */
    @Override
    public Task createNextOccurrence(LocalDateTime currentDateTime) {
        throw new UnsupportedOperationException("Todos cannot recur.");
    }

    /**
     * Returns this todo task in Alice's display format.
     *
     * @return The formatted todo task.
     */
    @Override
    public String toString() {
        return "[T]" + super.toString();
    }
}
