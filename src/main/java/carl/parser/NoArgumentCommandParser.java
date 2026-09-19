package carl.parser;

import java.util.function.Supplier;

import carl.commands.Command;
import carl.exceptions.CarlCommandException;

/**
 * Parses a command that does not accept any arguments.
 *
 * @param <T> command type produced by this parser.
 */
public class NoArgumentCommandParser<T extends Command> implements Parser<T> {
    private final String commandWord;
    private final Supplier<T> commandSupplier;

    /**
     * Creates a parser for an argument-free command.
     *
     * @param commandWord command name shown in validation errors.
     * @param commandSupplier factory for the parsed command.
     */
    public NoArgumentCommandParser(String commandWord, Supplier<T> commandSupplier) {
        this.commandWord = commandWord;
        this.commandSupplier = commandSupplier;
    }

    @Override
    public T parse(String input) throws CarlCommandException {
        if (!input.isBlank()) {
            throw new CarlCommandException("`" + commandWord + "` does not accept extra arguments.");
        }
        return commandSupplier.get();
    }
}
