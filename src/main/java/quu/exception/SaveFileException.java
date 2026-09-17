package quu.exception;

/**
 * Thrown when the save file cannot be used for a reason the user has to fix themselves,
 * such as the path naming a folder or the file being unreadable.
 *
 * <p>A save file that simply does not exist yet is not one of these: that is reported as a
 * {@link java.io.FileNotFoundException} and handled by starting an empty list, because the
 * program creates the file on the first save.
 */
public class SaveFileException extends QuuException {

    /**
     * Creates the exception with a message naming the problem and what to do about it.
     *
     * @param message the explanation to show the user
     */
    public SaveFileException(String message) {
        super(message);
    }
}
