package magnus;

/**
 * Contains a user-facing Magnus reply and its presentation state.
 *
 * @param message The text to show to the user.
 * @param isError Whether the reply reports a command-processing error.
 */
public record MagnusResponse(String message, boolean isError) {
    /**
     * Creates a normal reply.
     *
     * @param message The successful response text.
     * @return A non-error response.
     */
    public static MagnusResponse createSuccess(String message) {
        return new MagnusResponse(message, false);
    }

    /**
     * Creates an error reply.
     *
     * @param message The error text.
     * @return An error response.
     */
    public static MagnusResponse createError(String message) {
        return new MagnusResponse(message, true);
    }
}
