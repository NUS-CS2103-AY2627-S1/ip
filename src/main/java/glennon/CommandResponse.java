package glennon;

/**
 * Carries command output and its error status for GUI presentation.
 *
 * @param text formatted response without console dividers.
 * @param isError true when the command reported an error.
 */
public record CommandResponse(String text, boolean isError) {
}
