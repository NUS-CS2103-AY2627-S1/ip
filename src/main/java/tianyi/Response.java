package tianyi;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Stores a command result with separate heading, body, footer, and session status.
 */
public class Response {
    private final String header;
    private final String message;
    private final String footer;
    private final boolean isExit;
    private final boolean isError;

    /**
     * Creates a successful reply with an optional header and a message body.
     *
     * @param header Reply title, or an empty string for a reply without a title.
     * @param message Reply body.
     */
    public Response(String header, String message) {
        this(header, message, "", false, false);
    }

    /**
     * Creates a successful reply with separately styled main and footer content.
     *
     * @param header Reply title, or an empty string for a reply without a title.
     * @param message Main reply body.
     * @param footer Supplementary text displayed after the main body.
     */
    public Response(String header, String message, String footer) {
        this(header, message, footer, false, false);
    }

    /**
     * Creates a reply with explicit content and status.
     *
     * @param header Reply title.
     * @param message Reply body.
     * @param footer Supplementary text displayed after the main body.
     * @param isExit Whether the application should exit after showing this reply.
     * @param isError Whether this reply reports a failed command.
     */
    private Response(String header, String message, String footer, boolean isExit, boolean isError) {
        this.header = header;
        this.message = message;
        this.footer = footer;
        this.isExit = isExit;
        this.isError = isError;
    }

    /**
     * Creates an error reply without splitting its explanation or correction guidance.
     *
     * @param message Complete error message.
     * @return Error reply without a normal header.
     */
    public static Response error(String message) {
        return new Response("", message, "", false, true);
    }

    /**
     * Creates a farewell reply that ends the session.
     *
     * @param message Farewell message.
     * @return Reply requesting application shutdown.
     */
    public static Response exit(String message) {
        return new Response("", message, "", true, false);
    }

    /**
     * Returns the reply title, or an empty string when there is no title.
     *
     * @return Reply title or an empty string.
     */
    public String getHeader() {
        return header;
    }

    /**
     * Returns the reply body without its title.
     *
     * @return Reply body.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns supplementary text displayed after the main reply body.
     *
     * @return Reply footer, or an empty string when no footer is present.
     */
    public String getFooter() {
        return footer;
    }

    /**
     * Combines the title, body, and footer for plain-text console display.
     *
     * @return Complete reply without extra separators for missing content.
     */
    public String getFullMessage() {
        return Stream.of(header, message, footer)
                .filter(part -> !part.isEmpty())
                .collect(Collectors.joining("\n"));
    }

    /**
     * Reports whether this reply describes a failed command.
     *
     * @return Whether the reply represents an error.
     */
    public boolean isError() {
        return isError;
    }

    /**
     * Reports whether the application should exit after this reply.
     *
     * @return Whether the application should exit.
     */
    public boolean isExit() {
        return isExit;
    }
}
