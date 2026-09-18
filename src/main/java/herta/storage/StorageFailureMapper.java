package herta.storage;

import java.nio.file.AccessDeniedException;
import java.nio.file.FileSystemException;
import java.nio.file.NoSuchFileException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Converts platform-specific storage failures into stable user-facing wording.
 */
final class StorageFailureMapper {
    static final String EXTERNAL_CHANGE_ERROR = "the data file changed outside Herta; "
            + "reload before saving.";
    private static final Logger LOGGER = Logger.getLogger(StorageFailureMapper.class.getName());

    private StorageFailureMapper() {
        // Utility class; do not instantiate.
    }

    /** Maps a storage exception to the message shown to users. */
    static String map(Exception exception) {
        if (exception instanceof AccessDeniedException || exception instanceof SecurityException) {
            return "permission denied.";
        }
        if (exception instanceof NoSuchFileException) {
            return "data file is missing.";
        }
        if (exception instanceof FileSystemException fileSystemException
                && fileSystemException.getReason() != null
                && fileSystemException.getReason().toLowerCase().contains("lock")) {
            return "data file is locked.";
        }
        String reason = exception.getMessage();
        if (reason != null && reason.toLowerCase().contains("exceeds")) {
            return "data file is too large.";
        }
        if (reason != null && reason.toLowerCase().contains("space")) {
            return "disk is full.";
        }
        if (reason != null && reason.contains(EXTERNAL_CHANGE_ERROR)) {
            return EXTERNAL_CHANGE_ERROR;
        }
        return "an I/O failure occurred.";
    }

    /** Logs technical details without exposing platform paths or exception text to users. */
    static void log(String message, Exception exception) {
        LOGGER.log(Level.WARNING, message, exception);
    }
}
