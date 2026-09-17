package quu.exception;

/**
 * Thrown when a command that takes no arguments is given some anyway.
 *
 * <p>Without this, trailing text makes a known command look unknown: {@code bye now} used
 * to be reported as {@code I don't know what "bye" does}, which is not true.
 */
public class UnexpectedArgumentException extends QuuException {

    /**
     * Creates the exception, naming the command that takes no arguments.
     *
     * @param command the command word, as the program knows it
     */
    public UnexpectedArgumentException(String command) {
        super(String.format("%s takes no arguments. Just type: %s", command, command));
    }
}
