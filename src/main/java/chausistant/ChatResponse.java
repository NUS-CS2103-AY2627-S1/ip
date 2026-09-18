package chausistant;

import java.util.Objects;

/**
 * Represents one chatbot response together with the visual treatment it needs.
 *
 * <p>The console can display all messages as text, but the graphical interface
 * uses the response type to draw errors differently from ordinary replies.</p>
 */
public record ChatResponse(String text, Type type) {

    /** Identifies the visual importance of a chatbot response. */
    public enum Type {
        STANDARD,
        ERROR
    }

    /** Validates the data that describes one response. */
    public ChatResponse {
        Objects.requireNonNull(text);
        Objects.requireNonNull(type);
    }

    /** Returns whether this response represents an error. */
    public boolean isError() {
        return type == Type.ERROR;
    }
}
