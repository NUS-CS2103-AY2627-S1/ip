package megia.exception;

/**
 * Reports invalid command syntax or values supplied by the user.
 */
public class UserInputException extends MegiaException {
    /**
     * Creates an error for invalid command syntax or values.
     * Message arguments are supplied in the order expected by the localized template.
     * An explicitly null argument array is treated as empty.
     *
     * @param errorCode Identifies the message to display.
     * @param messageArguments Values interpolated into the message.
     */
    public UserInputException(ErrorCode errorCode, Object... messageArguments) {
        super(errorCode, messageArguments);
    }
}
