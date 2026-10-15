package dude.command.duplicate;

import java.util.Objects;

/**
 * Describes the action selected for a duplicate task.
 *
 * @param action    Selected action.
 * @param taskIndex Zero-based task index for an edit, or -1 otherwise.
 */
public record DuplicateResolution(Action action, int taskIndex) {
    /** Available duplicate-resolution actions. */
    public enum Action {
        /** Replace an existing matching task. */
        EDIT,
        /** Add the new task despite the duplicate. */
        ADD,
        /** Do not change the task list. */
        CANCEL
    }

    /**
     * Validates the index invariant for the selected action.
     *
     * @param action    Selected action.
     * @param taskIndex Zero-based edit index, or -1 for non-edit actions.
     */
    public DuplicateResolution {
        Objects.requireNonNull(action, "action");
        if (action == Action.EDIT && taskIndex < 0) {
            throw new IllegalArgumentException("Edit resolution requires a task index");
        }
        if (action != Action.EDIT && taskIndex != -1) {
            throw new IllegalArgumentException("Non-edit resolution cannot contain a task index");
        }
    }

    /**
     * Creates an add resolution.
     *
     * @return Add resolution.
     */
    public static DuplicateResolution add() {
        return new DuplicateResolution(Action.ADD, -1);
    }

    /**
     * Creates a cancel resolution.
     *
     * @return Cancel resolution.
     */
    public static DuplicateResolution cancel() {
        return new DuplicateResolution(Action.CANCEL, -1);
    }

    /**
     * Creates an edit resolution.
     *
     * @param taskIndex Zero-based task index.
     * @return Edit resolution.
     */
    public static DuplicateResolution edit(int taskIndex) {
        return new DuplicateResolution(Action.EDIT, taskIndex);
    }
}
