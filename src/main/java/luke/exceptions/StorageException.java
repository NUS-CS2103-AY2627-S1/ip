package luke.exceptions;

/**
 * Represents a storage problem that should be shown to the user as a normal
 * chatbot error.
 */
public class StorageException extends UserInputException {
    /**
     * Creates a storage exception with a user-facing message and original cause.
     *
     * @param message message to show to the user
     * @param cause original storage failure
     */
    private StorageException(String message, Throwable cause) {
        super(message);
        initCause(cause);
    }

    /**
     * Creates an error for failures while loading saved tasks.
     *
     * @param cause original storage failure
     * @return formatted storage exception
     */
    public static StorageException loadFailed(Throwable cause) {
        return new StorageException("Failed to load task list from data:\n"
                + ">  %s".formatted(cause.getMessage()),
                cause);
    }

    /**
     * Creates an error for failures while saving tasks.
     *
     * @param cause original storage failure
     * @return formatted storage exception
     */
    public static StorageException saveFailed(Throwable cause) {
        return new StorageException("Failed to save task list to data:\n"
                + ">  %s".formatted(cause.getMessage()),
                cause);
    }
}
