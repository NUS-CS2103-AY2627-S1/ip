package chattingheads.exception;

/**
 * Represents an error caused by an invalid task number.
 */
public class InvalidTaskNumberException extends ChattingHeadsException {

    public InvalidTaskNumberException() {
        super("You may ask yourself\n\"Where does that task number go to?\"");
    }
}
