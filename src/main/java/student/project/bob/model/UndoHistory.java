package student.project.bob.model;

import java.util.Objects;
import java.util.Optional;

/**
 * Stores the inverse of the most recent state-changing command.
 */
public class UndoHistory {
    private UndoAction undoAction;

    /**
     * Creates an empty undo history.
     */
    public UndoHistory() {}

    /**
     * Replaces the current undo action with a new one.
     *
     * @param action operation that restores the previous state
     * @param message message to display after the operation is applied
     */
    public void record(Runnable action, String message) {
        undoAction = new UndoAction(Objects.requireNonNull(action), Objects.requireNonNull(message));
    }

    /**
     * Applies and consumes the most recent undo action.
     *
     * @return the display message for the applied action, or an empty optional when there is no action
     */
    public Optional<String> undo() {
        if (undoAction == null) {
            return Optional.empty();
        }

        UndoAction action = undoAction;
        undoAction = null;
        action.action().run();
        return Optional.of(action.message());
    }

    private record UndoAction(Runnable action, String message) {}
}
