package carl.parser;

import carl.commands.Command;
import carl.exceptions.CarlException;

/**
 * Represents a parser that interprets user input strings to produce executable commands.
 *
 * @param <T> Type of {@code Command} produced by this parser.
 */
@FunctionalInterface
public interface Parser<T extends Command> {
    /**
     * Parses the specified input string into a command of type {@code T}.
     *
     * @param input Raw input string to be parsed.
     * @return Command object of type {@code T}.
     * @throws CarlException If the input format is invalid or cannot be parsed.
     */
    T parse(String input) throws CarlException;
}
