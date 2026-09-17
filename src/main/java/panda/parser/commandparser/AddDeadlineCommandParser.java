package panda.parser.commandparser;

import java.time.temporal.Temporal;

import panda.command.AddDeadlineCommand;
import panda.command.Command;
import panda.exception.parser.InvalidArgumentException;
import panda.util.datetime.DateTimeHelper;

/**
 * Parses arguments for a command that adds a deadline task.
 */
public class AddDeadlineCommandParser implements CommandParser {

    /**
     * Parses the deadline description and due date from the supplied arguments.
     *
     * @param arguments Text containing description and {@code /by DATE}.
     * @return An {@link AddDeadlineCommand} with the parsed description and due date.
     * @throws InvalidArgumentException If the syntax is malformed or the date format is invalid.
     */
    @Override
    public Command parseArguments(String arguments) throws InvalidArgumentException {
        String[] deadlineParts = arguments.split("\\s+/by\\s+", 2);
        if (deadlineParts.length != 2 || deadlineParts[0].isBlank() || deadlineParts[1].isBlank()) {
            throw new InvalidArgumentException("deadline", "deadline DESCRIPTION /by DATE");
        }
        String dateString = deadlineParts[1].strip();
        Temporal dueDate = DateTimeHelper.parseInput(dateString);
        return new AddDeadlineCommand(deadlineParts[0].strip(), dueDate);
    }
}
