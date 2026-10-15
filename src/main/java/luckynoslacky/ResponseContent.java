package luckynoslacky;

/**
 * Represents the content of a response returned by the chatbot.
 */
public sealed interface ResponseContent permits TextContent, TaskContent {
    /**
     * Returns the text representation used by the command-line interface.
     *
     * @return user-facing response text
     */
    String message();
}
