package parser;

import exception.FriedbergException;

/**
 * Handles the 'help' command for Friedberg.
 * Lists all supported commands with a short description of each command.
 */
public class HelpCommand implements Command {
    private static final String HELP_MESSAGE = "Commands available in Friedberg:\n"
            + "todo DESCRIPTION - Add a to-do task.\n"
            + "deadline DESCRIPTION /by DATE - Add a task with a deadline.\n"
            + "event DESCRIPTION /from FROM_DATE /to TO_DATE - Add an event between two dates.\n"
            + "list - List all active tasks.\n"
            + "find QUERY - Find active tasks containing the query.\n"
            + "mark TASK_INDEX - Mark an active task as done.\n"
            + "unmark TASK_INDEX - Mark an active task as not done.\n"
            + "delete TASK_INDEX - Permanently delete an active task.\n"
            + "archive TASK_INDEX - Move an active task to the archive.\n"
            + "alist - List all archived tasks.\n"
            + "unarchive TASK_INDEX - Restore an archived task.\n"
            + "help - Show this command summary.\n"
            + "bye - Exit Friedberg.\n"
            + "Dates must use the yyyy-MM-dd format.";

    @Override
    public boolean isCommand(String userInput) {
        return ParserUtil.hasCommandWord(userInput, "help");
    }

    @Override
    public String execute(String userInput, CommandContext commandContext) throws FriedbergException {
        ParserUtil.parseNoArgumentCommand(userInput, "help");
        return HELP_MESSAGE;
    }

    @Override
    public boolean isBye() {
        return false;
    }
}
