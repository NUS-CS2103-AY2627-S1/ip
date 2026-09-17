package panda.exception.parser;

import panda.exception.ApplicationException;

/**
 * Signals that Panda could not convert user input into a command.
 */
public abstract class ParseException extends ApplicationException {

    /**
     * Creates a parsing exception with a user-facing message.
     *
     * @param message Explanation of the input problem.
     */
    public ParseException(String message) {
        super(message);
    }

    /**
     * Creates a parsing exception with a user-facing message and underlying cause.
     *
     * @param message Explanation of the input problem.
     * @param cause Error that caused this exception.
     */
    protected ParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
