package ari;

/**
 * Carries display text and explicit outcome flags to console and graphical clients.
 *
 * @param message Text to display without inferring behavior from its wording.
 * @param isError Whether the command was rejected.
 * @param shouldExit Whether the client should close after displaying the response.
 */
public record CommandResult(String message, boolean isError, boolean shouldExit) {
}
