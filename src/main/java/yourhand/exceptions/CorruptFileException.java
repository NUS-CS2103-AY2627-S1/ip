package yourhand.exceptions;

/**
 * Represents invalid task data found while loading YourHand's saved data file.
 */
public class CorruptFileException extends Exception {
    /**
     * Creates an exception describing malformed saved data.
     *
     * @param cause The original parsing error.
     */
    public CorruptFileException(Throwable cause) {
        super("A saved task entry is malformed.", cause);
    }
}
