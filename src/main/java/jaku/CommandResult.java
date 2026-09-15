package jaku;

import java.util.Objects;

/**
 * Contains the text and presentation category produced by one Jaku command.
 *
 * @param text complete user-facing response text
 * @param kind presentation category for a user interface
 */
public record CommandResult(String text, MessageKind kind) {
    /**
     * Creates a command result with non-null text and category.
     *
     * @param text complete response text
     * @param kind category used by a user interface
     */
    public CommandResult {
        Objects.requireNonNull(text);
        Objects.requireNonNull(kind);
    }
}
