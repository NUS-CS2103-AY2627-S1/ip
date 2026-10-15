package cxception;

/**
 * Thrown when accessing an out-of-bounds index.
 */
public class MarkOutException extends Exception {
    /**
     * Constructs a {@code MarkOutException} with a default out-of-bounds message.
     */
    public MarkOutException() {
        super("Can't access this index!");
    }
}
