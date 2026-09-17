package panda.parser.commandparser;

import panda.command.Command;
import panda.command.UnmarkTaskCommand;
import panda.exception.parser.InvalidArgumentException;

/**
 * Parses arguments for the {@code unmark} command.
 */
public class UnmarkTaskCommandParser implements CommandParser {

    /**
     * Parses the task number to unmark from the supplied arguments.
     *
     * @param arguments Text containing the one-based task number.
     * @return An {@link UnmarkTaskCommand} with the parsed task index.
     * @throws InvalidArgumentException If the argument is empty, non-numeric, or exceeds the integer range.
     */
    @Override
    public Command parseArguments(String arguments) throws InvalidArgumentException {
        int taskNumber = TaskNumberParser.parse(arguments, "unmark");
        return new UnmarkTaskCommand(taskNumber);
    }
}
