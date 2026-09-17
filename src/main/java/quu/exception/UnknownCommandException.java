package quu.exception;

/**
 * Thrown when the first word of the input does not name a command the chatbot knows.
 */
public class UnknownCommandException extends QuuException {

    /**
     * Creates the exception, quoting the unrecognised command back to the user alongside
     * the commands that would have been recognised.
     *
     * @param command the word that was not recognised
     * @param knownCommands the command words the chatbot understands, comma separated
     */
    public UnknownCommandException(String command, String knownCommands) {
        super(String.format("I don't know what \"%s\" does.%nI understand: %s",
                command, knownCommands));
    }
}
