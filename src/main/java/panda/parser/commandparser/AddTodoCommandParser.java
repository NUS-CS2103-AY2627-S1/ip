package panda.parser.commandparser;

import panda.command.AddTodoCommand;
import panda.command.Command;
import panda.exception.parser.InvalidArgumentException;

/**
 * Parses arguments for a command that adds a to-do task.
 */
public class AddTodoCommandParser implements CommandParser {

    /**
     * Parses the to-do description from the supplied arguments.
     *
     * @param arguments Description of the task.
     * @return An {@link AddTodoCommand} with the parsed description.
     * @throws InvalidArgumentException If the description is empty.
     */
    @Override
    public Command parseArguments(String arguments) throws InvalidArgumentException {
        if (arguments.isEmpty()) {
            throw new InvalidArgumentException("todo", "todo DESCRIPTION");
        }
        return new AddTodoCommand(arguments);
    }
}
