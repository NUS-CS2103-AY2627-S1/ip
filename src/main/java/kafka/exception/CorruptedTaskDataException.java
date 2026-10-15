package kafka.exception;

/**
 * Reports a task-data line that Kafka cannot safely turn back into a task.
 */
public class CorruptedTaskDataException extends KafkaException {
    /**
     * Creates an error that identifies corrupted saved data.
     *
     * @param message Explanation of where the corrupted data was found.
     */
    public CorruptedTaskDataException(String message) {
        super(message);
    }
}
