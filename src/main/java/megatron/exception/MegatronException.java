package megatron.exception;
/**
 * Represents an error caused by an invalid Megatron command.
 */
public class MegatronException extends Exception {
    /**
     * Creates an exception with an explanation for the user.
     *
     * @param message the explanation of the invalid command
     */
    public MegatronException(String message) {
        super(message);
    }
}
