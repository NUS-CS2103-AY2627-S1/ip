package carl.parser;

import carl.commands.Command;

/**
 * Represents a functional interface for creating commands that operate on a task index.
 *
 * @param <T> The specific subtype of {@link Command} to be instantiated.
 */
@FunctionalInterface
public interface CommandIndexFunction<T extends Command> {
    /**
     * Instantiates a command with the specified task index.
     *
     * @param index The target task index.
     * @return An instance of the command initialized with the index.
     */
    T create(int index);
}
