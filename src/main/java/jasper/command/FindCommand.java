package jasper.command;

import jasper.JasperException;
import jasper.task.TaskList;

/**
 * Represents a command to find tasks containing a specific search phrase.
 */
public class FindCommand implements Command {
    /** Valid command format message */
    private static final String USAGE_MSG = "Usage: find <search-phrase>";
    /** Search phrase used to match tasks */
    private final String phrase;

    /**
     * Constructs a FindCommand and validates the search phrase.
     *
     * @param arg The argument string containing the search phrase.
     * @throws JasperException If the argument string is empty.
     */
    public FindCommand(String arg) throws JasperException {
        if (arg.isEmpty()) {
            throw new JasperException(USAGE_MSG);
        }
        phrase = arg;
    }

    @Override
    public CommandResult execute(TaskList tasks) {
        if (tasks.isEmpty()) {
            return new CommandResult(CommandType.FIND, "No tasks here! Add some to search through.");
        }
        String response = tasks.find(phrase);
        if (response.isEmpty()) {
            return new CommandResult(CommandType.FIND, "Where might the matching tasks be?");
        }
        return new CommandResult(CommandType.FIND, "Matching tasks, here you go:\n" + response);
    }
}
