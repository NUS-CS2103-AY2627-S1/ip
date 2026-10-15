package dingleberry.exception;

/**
 * Represents an invalid command or task input entered for Dingleberry.
 */
public class DingleberryException extends Exception {
    /** Stores the classification used to select the UI error message. */
    private final ErrorType errorType;

    /**
     * Creates an exception for incorrect command parameters.
     *
     * @param message the user-visible validation failure.
     */
    public DingleberryException(final String message) {
        this(message, ErrorType.INCORRECT_PARAMETERS);
    }

    /**
     * Creates an exception with the supplied user-input error type.
     *
     * @param message the user-visible validation failure.
     * @param type the classification used by the UI.
     */
    public DingleberryException(final String message, final ErrorType type) {
        super(message);
        this.errorType = type;
    }

    /**
     * Returns whether this exception represents an unrecognized command.
     *
     * @return true when the command word is unknown.
     */
    public boolean isWrongCommand() {
        return errorType == ErrorType.WRONG_COMMAND;
    }

    /** Categorizes user-input errors. */
    public enum ErrorType {
        /** Indicates that a recognized command has invalid parameters. */
        INCORRECT_PARAMETERS,
        /** Indicates that the supplied command word is not recognized. */
        WRONG_COMMAND
    }
}
