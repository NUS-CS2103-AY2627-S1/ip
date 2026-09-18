package herta.exception;

/**
 * Represents an input error that includes guidance for correcting command usage.
 */
public final class UsageGuidanceException extends HertaException {

    /**
     * Creates an exception with a user-facing usage explanation.
     *
     * @param message the usage explanation shown to the user.
     */
    public UsageGuidanceException(String message) {
        super(message);
    }
}
