package student.project.bob.exception;

/**
 * Represents an error caused by invalid input to Bob.
 *
 * <p>This checked exception keeps input validation separate from the user
 * interface: command-processing code can report a problem, while the main
 * loop decides how to display it and continue running.</p>
 */
public class BobException extends Exception {

    /**
     * Creates an exception with a message suitable for showing to the user.
     *
     * @param message explanation of the input error
     */
    public BobException(String message) {
        super(message);
    }
}
