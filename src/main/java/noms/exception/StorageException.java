package noms.exception;

/** Indicates that Noms could not persist a task-list change. */
public class StorageException extends NomsException {
    /**
     * Creates an exception describing a save failure.
     *
     * @param cause the input/output failure raised by storage
     */
    public StorageException(Throwable cause) {
        super("Noms couldn't save the menu to disk: " + cause.getMessage());
    }
}
