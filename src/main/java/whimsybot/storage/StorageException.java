package whimsybot.storage;

/** Signals that Whimsy Bot could not read or write its saved task data. */
public class StorageException extends Exception {

    /** Creates a storage exception with a user-facing message and root cause. */
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
