package meka.exception;

/**
 * Represents invalid task data found while loading the saved task file.
 */
public class DataFileException extends Exception {

    /**
     * Creates an exception describing invalid saved data.
     *
     * @param message explanation of the invalid data.
     */
    public DataFileException(String message) {
        super(message);
    }
}
