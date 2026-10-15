package spoon.exception;

/**
 * Thrown when the file containing the list of tasks is corrupted (not formatted correctly).
 */
public class FileCorruptedException extends SpoonException {

    // Constructor
    public FileCorruptedException(int lineNumber) {
        super("Oh no, your file seems to be corrupted! Skipping line " + lineNumber + "...");
    }
}
