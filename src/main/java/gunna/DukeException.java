package gunna;

/**
 * Represents exceptions specific to the Gunna application.
 * Used to signal errors in command processing, task operations, etc.
 */
public class DukeException extends Exception {

    /**
     * Constructs a DukeException with the specified error message.
     *
     * @param message The error message describing what went wrong.
     */
    public DukeException(String message) {
        super(message);
    }
}
