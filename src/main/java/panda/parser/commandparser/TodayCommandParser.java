package panda.parser.commandparser;

import panda.command.Command;
import panda.command.TodayCommand;
import panda.exception.parser.InvalidArgumentException;

/**
 * Parses arguments for the {@code today} command.
 */
public class TodayCommandParser implements CommandParser {

    /**
     * Validates that no extra arguments are passed to the {@code today} command.
     *
     * @param arguments Text following the today command keyword.
     * @return A {@link TodayCommand} instance.
     * @throws InvalidArgumentException If extraneous arguments are supplied.
     */
    @Override
    public Command parseArguments(String arguments) throws InvalidArgumentException {
        if (!arguments.isEmpty()) {
            throw new InvalidArgumentException("today", "today");
        }
        return new TodayCommand();
    }
}
