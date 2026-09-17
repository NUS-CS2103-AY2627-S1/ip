package botavius.exception;

/**
 * Reports an invalid command or task detail entered in the Botavius
 * application.
 */
public class BotaviusException extends RuntimeException {
    /**
     * Creates an exception with a message describing the problem.
     *
     * @param message explanation of the error
     */
    public BotaviusException(String message) {
        super(message);
    }
}
