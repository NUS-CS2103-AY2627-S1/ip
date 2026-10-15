package peanutbuttercat;

/**
 * Represents an input error that peanutbuttercat can explain to the user.
 */
public class PeanutButterCatException extends Exception {

    /**
     * Creates an exception with a user-friendly explanation of the error.
     *
     * @param message Explanation to show to the user.
     */
    public PeanutButterCatException(String message) {
        super(message);
    }
}
