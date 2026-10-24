package shannon.exception;

/**
 * Thrown when the task list cannot be written to, or read from, the save file.
 * <p>
 * Extends {@link ShannonException} so a storage failure is reported through the same single
 * {@code catch} as every other error, and the chatbot keeps running afterwards.
 */
public class StorageException extends ShannonException {

    /**
     * Creates an exception with an already-worded message.
     * <p>
     * Private, with each case built by one of the named factory methods below: saving and
     * loading need different wording, and a constructor cannot be overloaded on
     * {@code (String, String)} twice.
     *
     * @param message the text to show the user
     */
    private StorageException(String message) {
        super(message);
    }

    /**
     * Creates the exception for a save that failed.
     *
     * @param filePath the save file that could not be written, so the user knows where to look
     * @param reason   the underlying failure, e.g. {@code "Permission denied"}
     * @return the exception, ready to be thrown
     */
    public static StorageException whileSaving(String filePath, String reason) {
        return new StorageException("I couldn't save your tasks to " + filePath
                + " (" + reason + "). Your list is up to date here,"
                + " but it may be lost when Shannon exits.");
    }

    /**
     * Creates the exception for a load that failed.
     *
     * @param filePath the save file that could not be read
     * @param reason   the underlying failure, e.g. {@code "Is a directory"}
     * @return the exception, ready to be thrown
     */
    public static StorageException whileLoading(String filePath, String reason) {
        return new StorageException("I couldn't read your saved tasks from " + filePath
                + " (" + reason + "). Starting with an empty list.");
    }

    /**
     * Creates the exception for a save that was refused, because it would overwrite tasks in a
     * file that could be neither fully loaded nor backed up.
     *
     * @param filePath the save file that is being protected
     * @return the exception, ready to be thrown
     */
    public static StorageException whenSavingIsUnsafe(String filePath) {
        return new StorageException("I haven't saved this change, because that would overwrite"
                + " tasks in " + filePath + " that I couldn't read. Your list is up to date here;"
                + " please fix the file and restart me to save again.");
    }
}
