package friday;

import java.util.Objects;

/**
 * Carries a command's unchanged console text and its presentation severity.
 *
 * @param text complete response, including console separators.
 * @param severity presentation category, independent of the response wording.
 */
public record Response(String text, Severity severity) {
    /** Distinguishes ordinary output, invalid commands, and persistence warnings. */
    public enum Severity {
        NORMAL, ERROR, WARNING
    }

    /** Rejects incomplete responses before they reach a user interface. */
    public Response {
        Objects.requireNonNull(text);
        Objects.requireNonNull(severity);
    }
}
