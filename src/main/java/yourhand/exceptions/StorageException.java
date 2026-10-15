package yourhand.exceptions;

/** Represents an environment failure while saving YourHand data. */
public class StorageException extends YourHandException {
    /** Creates the standard user-facing save failure message. */
    public StorageException() {
        super("I couldn't save your tasks. Please check the data folder.");
    }
}
