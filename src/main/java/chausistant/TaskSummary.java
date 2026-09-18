package chausistant;

import java.util.Objects;

/**
 * Holds the task information displayed in Chausistant's dashboard summary.
 *
 * <p>This keeps the graphical interface from depending directly on the
 * mutable task model.</p>
 */
public record TaskSummary(String text, boolean isCompleted) {

    /** Validates the immutable task-summary data. */
    public TaskSummary {
        Objects.requireNonNull(text);
    }
}
