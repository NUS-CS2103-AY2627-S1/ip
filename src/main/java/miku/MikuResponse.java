package miku;

import java.util.Objects;

/** Represents a message from Miku and whether it reports a problem. */
public record MikuResponse(String text, boolean isError) {
    /** Creates a response with text that can be displayed by the GUI. */
    public MikuResponse {
        Objects.requireNonNull(text);
    }
}
