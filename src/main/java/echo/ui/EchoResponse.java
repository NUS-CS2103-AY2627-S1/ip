package echo.ui;

/**
 * Carries response text together with its presentation type.
 *
 * @param text Text to display.
 * @param type Presentation type of the response.
 */
public record EchoResponse(String text, ResponseType type) {
}
