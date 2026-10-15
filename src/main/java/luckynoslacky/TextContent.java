package luckynoslacky;

import java.util.Objects;

/**
 * Represents an ordinary text-only chatbot response.
 *
 * @param message user-facing response text
 */
public record TextContent(String message) implements ResponseContent {
    /** Validates the response text. */
    public TextContent {
        Objects.requireNonNull(message, "Response message cannot be null.");
    }
}
