package wally;

/** Indicates that a background command specifies an unsupported colour. */
public class InvalidBackgroundColourException extends Exception {
    /** Creates an error listing the supported background colours. */
    public InvalidBackgroundColourException() {
        super("Only black, white and blue supported");
    }
}
