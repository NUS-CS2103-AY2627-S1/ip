package james.exception;

/**
 * Signals an error caused by invalid or malformed user input or data.
 */
public class UserInputException extends Exception {
    /**
     * Distinguishes invalid input from requests that cannot be completed.
     */
    public enum Category { INPUT, DUPLICATE, STORAGE }

    private final Category category;

    /**
     * Constructs a UserInputException with the specified error message.
     *
     * @param message Description of the user error.
     */
    public UserInputException(String message) {
        this(message, Category.INPUT);
    }

    /**
     * Constructs an error with an explicit category for response selection.
     *
     * @param message Description of the error.
     * @param category Reason the request failed.
     */
    public UserInputException(String message, Category category) {
        super(message);
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }
}
