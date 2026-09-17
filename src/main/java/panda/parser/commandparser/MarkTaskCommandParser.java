package panda.parser.commandparser;

import panda.command.Command;
import panda.command.MarkTaskCommand;
import panda.exception.parser.InvalidArgumentException;

/**
 * Parses arguments for the {@code mark} command.
 */
public class MarkTaskCommandParser implements CommandParser {

    /**
     * Parses the task number to mark from the supplied arguments.
     *
     * @param arguments Text containing the one-based task number.
     * @return A {@link MarkTaskCommand} with the parsed task index.
     * @throws InvalidArgumentException If the argument is empty, non-numeric, or exceeds the integer range.
     */
    @Override
    public Command parseArguments(String arguments) throws InvalidArgumentException {
        int taskNumber = TaskNumberParser.parse(arguments, "mark");
        return new MarkTaskCommand(taskNumber);
    }
}
