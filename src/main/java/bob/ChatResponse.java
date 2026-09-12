package bob;

/**
 * Represents the result of processing one user command.
 *
 * @param text response to display to the user.
 * @param isExit whether the application should end the current session.
 * @param responseType semantic type used to present the response.
 */
public record ChatResponse(String text, boolean isExit, ResponseType responseType) {
}
