package groot;

/**
 * Carries response text and error status together so the GUI can style failures reliably.
 *
 * @param text Message returned by command processing.
 * @param isError Whether command processing failed.
 */
public record CommandResponse(String text, boolean isError) {
}
