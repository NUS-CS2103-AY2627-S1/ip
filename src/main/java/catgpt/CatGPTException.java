package catgpt;

/**
 * Represents an error caused by an invalid command entered by the user.
 */
public class CatGPTException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with a user-friendly explanation of the error.
     *
     * @param message The explanation shown to the user.
     */
    public CatGPTException(String message) {
        super(message);
    }
}
