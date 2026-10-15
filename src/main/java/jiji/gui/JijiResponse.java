package jiji.gui;

/**
 * Encapsulates a chatbot response along with metadata indicating whether the response
 * represents an error condition.
 */
public class JijiResponse {

    private final String message;
    private final boolean isError;

    /**
     * Constructs a {@code JijiResponse} with the given message and error status.
     *
     * @param message The response message text.
     * @param isError True if the response represents an error, false otherwise.
     */
    public JijiResponse(String message, boolean isError) {
        this.message = message;
        this.isError = isError;
    }

    /**
     * Returns the response message text.
     *
     * @return The message string.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns whether this response represents an error.
     *
     * @return True if the response is an error, false otherwise.
     */
    public boolean isError() {
        return isError;
    }
}
