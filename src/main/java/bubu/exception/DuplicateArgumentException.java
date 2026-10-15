package bubu.exception;

/** Indicates that a command contains the same named argument more than once. */
public class DuplicateArgumentException extends BubuException {
    private static final String DUPLICATE_ARGUMENT_MESSAGE =
            "Meow! The /%s argument should only be specified once.";

    /**
     * Creates an error for a repeated command argument.
     *
     * @param argumentName repeated argument name without its leading slash.
     */
    public DuplicateArgumentException(String argumentName) {
        super(String.format(DUPLICATE_ARGUMENT_MESSAGE, argumentName));
    }
}
