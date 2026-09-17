package panda.parser.commandparser;

import panda.command.Command;
import panda.command.ListTasksCommand;
import panda.exception.parser.InvalidArgumentException;

/**
 * Parses arguments for the {@code list} command.
 */
public class ListTasksCommandParser implements CommandParser {

    /**
     * Validates that no extra arguments are passed to the {@code list} command.
     *
     * @param arguments Text following the list command keyword.
     * @return A {@link ListTasksCommand} instance.
     * @throws InvalidArgumentException If extraneous arguments are supplied.
     */
    @Override
    public Command parseArguments(String arguments) throws InvalidArgumentException {
        if (!arguments.isEmpty()) {
            throw new InvalidArgumentException("list", "list");
        }
        return new ListTasksCommand();
    }
}
