package yappa.parser;

import yappa.command.Command;
import yappa.exception.YappaException;

/**
 * Parses command arguments into a command.
 */
@FunctionalInterface
public interface CommandParser {

    /**
     * Parses command arguments into a command.
     *
     * @param arguments Command arguments to parse.
     * @return Parsed command.
     * @throws YappaException If the arguments are invalid.
     */
    Command parse(String arguments) throws YappaException;
}