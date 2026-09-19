package carl.parser;

import carl.commands.Command;
import carl.exceptions.CarlCommandException;
import carl.exceptions.CarlException;

/**
 * Parses user input containing a task index and instantiates the corresponding command.
 *
 * @param <T> The specific subtype of {@link Command} produced by this parser.
 */
public class TaskIndexParser<T extends Command> implements Parser<T> {
    private final CommandIndexFunction<T> commandFunction;

    /**
     * Constructs a {@code TaskIndexParser} with the specified command factory function.
     *
     * @param commandFunction The factory used to instantiate the command using a parsed index.
     */
    public TaskIndexParser(CommandIndexFunction<T> commandFunction) {
        this.commandFunction = commandFunction;
    }

    /**
     * Parses the user input into an integer index and constructs the command.
     *
     * @param input Raw argument string representing the task index.
     * @return The instantiated command of type {@code T}.
     * @throws CarlException If the input is empty or cannot be parsed into a valid integer.
     */
    @Override
    public T parse(String input) throws CarlException {
        if (input.isEmpty()) {
            throw new CarlCommandException("Enter a task number. Use `list` to view task numbers.");
        }

        if (!input.matches("[1-9]\\d*")) {
            throw new CarlCommandException("Task numbers must be positive whole numbers, e.g. `1`.");
        }

        try {
            return commandFunction.create(Integer.parseInt(input));
        } catch (NumberFormatException e) {
            throw new CarlCommandException("That task number is too large.");
        }
    }
}
