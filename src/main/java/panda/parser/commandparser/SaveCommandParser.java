package panda.parser.commandparser;

import panda.command.Command;
import panda.command.SaveBeforeExitCommand;
import panda.exception.parser.InvalidArgumentException;

/**
 * Parses the graphical interface's command for saving before exit confirmation.
 */
public class SaveCommandParser implements CommandParser {

    /**
     * Validates that the save command has no arguments.
     *
     * @param arguments Text following the save command.
     * @return A {@link SaveBeforeExitCommand} instance.
     * @throws InvalidArgumentException If extraneous arguments are supplied.
     */
    @Override
    public Command parseArguments(String arguments) throws InvalidArgumentException {
        if (!arguments.isEmpty()) {
            throw new InvalidArgumentException("save", "save");
        }
        return new SaveBeforeExitCommand();
    }
}
