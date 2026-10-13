package baymax.ui;

import java.io.IOException;
import java.util.function.Function;

/** Saves before closing and applies the user's decision when saving fails. */
public final class CloseGuard {
    /** Choices available after a failed save. */
    public enum Decision {
        RETRY, CANCEL, EXIT_WITHOUT_SAVING
    }

    /** A save operation that can report a storage failure. */
    @FunctionalInterface
    public interface SaveAction {
        /** Saves pending changes, or throws if they cannot be saved. */
        void save() throws IOException;
    }

    private CloseGuard() {
        // Utility class.
    }

    /**
     * Returns whether closing is safe or explicitly authorized after a save failure.
     * A canceled or dismissed prompt leaves the application open.
     */
    public static boolean canClose(SaveAction saveAction, Function<IOException, Decision> prompt) {
        while (true) {
            try {
                saveAction.save();
                return true;
            } catch (IOException exception) {
                Decision decision = prompt.apply(exception);
                if (decision != Decision.RETRY) {
                    return decision == Decision.EXIT_WITHOUT_SAVING;
                }
            }
        }
    }
}
