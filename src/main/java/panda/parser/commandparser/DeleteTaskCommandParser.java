package panda.parser.commandparser;

import panda.command.Command;
import panda.command.DeleteTaskCommand;
import panda.exception.parser.InvalidArgumentException;

/**
 * Parses arguments for the {@code delete} command.
 */
public class DeleteTaskCommandParser implements CommandParser {

    /**
     * Parses the task number to delete from the supplied arguments.
     *
     * @param arguments Text containing the one-based task number.
     * @return A {@link DeleteTaskCommand} with the parsed task index.
     * @throws InvalidArgumentException If the argument is empty, non-numeric, or exceeds the integer range.
     */
    @Override
    public Command parseArguments(String arguments) throws InvalidArgumentException {
        int taskNumber = TaskNumberParser.parse(arguments, "delete");
        return new DeleteTaskCommand(taskNumber);
    }
}
