package panda.parser.commandparser;

import panda.command.ByeCommand;
import panda.command.Command;
import panda.exception.parser.InvalidArgumentException;

/**
 * Parses the graphical interface's command for terminating Panda after confirmation.
 */
public class ByeCommandParser implements CommandParser {

    /**
     * Validates that the bye command has no arguments.
     *
     * @param arguments Text following the bye command.
     * @return A {@link ByeCommand} instance.
     * @throws InvalidArgumentException If extraneous arguments are supplied.
     */
    @Override
    public Command parseArguments(String arguments) throws InvalidArgumentException {
        if (!arguments.isEmpty()) {
            throw new InvalidArgumentException("bye", "bye");
        }
        return new ByeCommand();
    }
}
