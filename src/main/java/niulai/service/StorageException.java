package niulai.service;

import java.io.IOException;
import java.util.Objects;

/**
 * Represents an expected storage failure with a safe message suitable for the user.
 */
public class StorageException extends IOException {
    /** Message used when loading was unsafe and later writes must remain disabled. */
    public static final String READ_BLOCKED_MESSAGE =
            "I couldn't read the task file safely. Task changes are disabled until you fix "
                    + "the file and restart NiuLai.";

    /** Message used when another process changes the task file during a session. */
    public static final String EXTERNAL_CHANGE_MESSAGE =
            "The task file changed outside NiuLai. Restart NiuLai before making changes.";

    /** Message used when a damaged file cannot be protected before repair. */
    public static final String BACKUP_FAILED_MESSAGE =
            "I couldn't back up the damaged task file, so your changes were not saved.";

    /** Message used for other expected save failures. */
    public static final String SAVE_FAILED_MESSAGE = "I couldn't save your tasks to disk.";

    private static final long serialVersionUID = 1L;

    /** The message that can be shown without exposing implementation details. */
    private final String userMessage;

    /**
     * Creates a storage exception without an underlying cause.
     *
     * @param userMessage the safe message to show the user
     */
    public StorageException(String userMessage) {
        super(Objects.requireNonNull(userMessage, "userMessage"));
        this.userMessage = userMessage;
    }

    /**
     * Creates a storage exception that retains its technical cause for diagnostics.
     *
     * @param userMessage the safe message to show the user
     * @param cause the underlying filesystem or decoding failure
     */
    public StorageException(String userMessage, Throwable cause) {
        super(Objects.requireNonNull(userMessage, "userMessage"), cause);
        this.userMessage = userMessage;
    }

    /**
     * Returns the safe explanation intended for the application interface.
     *
     * @return the user-facing failure message
     */
    public String getUserMessage() {
        return userMessage;
    }
}
