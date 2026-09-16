package discitrack.exception;

/**
 * Represents an error that can be shown to the DisciTrack user.
 */
public class DisciTrackException extends Exception {
    /**
     * Creates a DisciTrack exception with a user-facing message.
     *
     * @param message the message explaining the error.
     */
    public DisciTrackException(String message) {
        super(message);
    }
}
