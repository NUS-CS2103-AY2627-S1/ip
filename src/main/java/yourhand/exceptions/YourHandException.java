package yourhand.exceptions;

/**
 * Represents an error caused by invalid user input in YourHand.
 */
public class YourHandException extends Exception {
    /**
     * Creates an exception with a message suitable for display to the user.
     *
     * @param message Explanation of the invalid input and how to correct it.
     */
    public YourHandException(String message) {
        super(message);
    }
}
