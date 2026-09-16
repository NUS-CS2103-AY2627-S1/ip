package discitrack.exception;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.AtomicMoveNotSupportedException;

/**
 * Reports a failed save after the command has restored the original task state.
 */
public class SaveException extends DisciTrackException {
    /**
     * Creates an actionable save error while preserving its technical cause.
     *
     * @param cause the file operation that failed.
     */
    public SaveException(IOException cause) {
        super("UHOH! I could not save your tasks. No changes were made. " + recovery(cause));
        initCause(cause);
    }

    private static String recovery(IOException cause) {
        if (cause instanceof AccessDeniedException) {
            return "Access was denied. Check write permissions for the data folder and try again.";
        }
        if (cause instanceof AtomicMoveNotSupportedException) {
            return "This location cannot safely replace the save file. "
                    + "Use a local folder supporting atomic replacement.";
        }
        return "Check that the data folder is writable and has free space, then try again.";
    }
}
