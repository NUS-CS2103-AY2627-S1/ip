package kafka.exception;

/**
 * Reports command input that Kafka cannot parse.
 */
public class ParserException extends KafkaException {
    /**
     * Creates a parser error with guidance for the user.
     *
     * @param message Explanation of what was wrong with the command.
     */
    public ParserException(String message) {
        super(message);
    }
}
