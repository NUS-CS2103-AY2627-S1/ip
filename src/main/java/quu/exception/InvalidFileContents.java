package quu.exception;

/**
 * Thrown when a line of the save file does not match the expected format.
 */
public class InvalidFileContents extends QuuException {

    /**
     * Creates the exception.
     *
     * @param message description of what was wrong with the file
     */
    public InvalidFileContents(String message) {
        super(message);
    }
}
