package verity.storage;

/**
 * Signals that a line in the save file was not in the expected format and
 * could not be parsed back into a task.
 */
public class CorruptedSaveDataException extends Exception {

    /**
     * Creates an exception with the given message describing what was wrong with the line.
     *
     * @param message Message explaining what went wrong.
     */
    public CorruptedSaveDataException(String message) {
        super(message);
    }
}
