package kojispawn.exception;

/**
 * Represents an error caused by a command that Koji's Pawn cannot process.
 */
public class KojisPawnException extends Exception {
    /**
     * Creates an exception containing an explanation suitable for the user.
     *
     * @param message Explanation of the invalid command.
     */
    public KojisPawnException(String message) {
        super(message);
    }
}
