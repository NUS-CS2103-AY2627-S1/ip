package rocky;

/**
 * Signals a problem with user input or with reading/writing saved data
 * that Rocky can recover from by showing the user a friendly message,
 * rather than crashing.
 */
public class RockyException extends Exception {

    /**
     * Creates a RockyException carrying a message meant to be shown
     * directly to the user, by whichever UI (text or GUI) is in use.
     *
     * @param message the user-facing explanation of what went wrong.
     */
    public RockyException(String message) {
        super(message);
    }
}
