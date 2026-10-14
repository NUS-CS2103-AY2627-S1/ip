package dude.command.duplicate;

import java.util.List;
import java.util.Objects;

import dude.task.Task;

/**
 * Contains a candidate task and existing tasks with the same normalized description.
 *
 * @param candidate New task that the user attempted to add.
 * @param matches   Existing matching tasks and list indexes.
 */
public record DuplicateTaskConflict(Task candidate, List<DuplicateMatch> matches) {
    /**
     * Validates and snapshots the conflict details.
     *
     * @param candidate New task that the user attempted to add.
     * @param matches   Existing matching tasks and list indexes.
     */
    public DuplicateTaskConflict {
        Objects.requireNonNull(candidate, "candidate");
        matches = List.copyOf(Objects.requireNonNull(matches, "matches"));
    }

    /**
     * Represents a matching task and its zero-based position in the task list.
     *
     * @param index Zero-based task-list index.
     * @param task  Existing matching task.
     */
    public record DuplicateMatch(int index, Task task) {
        /**
         * Validates one duplicate match.
         *
         * @param index Zero-based task-list index.
         * @param task  Existing matching task.
         */
        public DuplicateMatch {
            if (index < 0) {
                throw new IllegalArgumentException("Duplicate index cannot be negative");
            }
            Objects.requireNonNull(task, "task");
        }
    }
}
