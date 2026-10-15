package alexa;

/** Represents an error caused by an invalid command entered into Alexa. */
public class AlexaException extends Exception {
    /**
     * Creates an exception with a message that can be shown to the user.
     *
     * @param message An explanation of how to correct the command.
     */
    public AlexaException(String message) {
        super(message);
    }
}
