package kafka.exception;

/**
 * Represents an expected error that Kafka can report to the user.
 */
public class KafkaException extends Exception {
    /**
     * Creates an expected error with a user-facing explanation.
     *
     * @param message Explanation Kafka should show the user.
     */
    public KafkaException(String message) {
        super(message);
    }

    /**
     * Creates an expected error while preserving the technical cause for debugging.
     *
     * @param message Explanation Kafka should show the user.
     * @param cause Lower-level problem that caused this error.
     */
    public KafkaException(String message, Throwable cause) {
        super(message, cause);
    }
}
