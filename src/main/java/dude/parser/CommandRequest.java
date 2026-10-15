package dude.parser;

import java.util.Objects;

import dude.command.core.CommandType;

/**
 * Represents the syntactic result of parsing one command line.
 *
 * @param type     Parsed command type.
 * @param argument Raw argument, or null when absent.
 */
public record CommandRequest(CommandType type, String argument) {
    /**
     * Validates that a parsed request has a command type.
     *
     * @param type     Parsed command type.
     * @param argument Raw argument, or null when absent.
     */
    public CommandRequest {
        Objects.requireNonNull(type, "type");
    }
}
