package parser;

import exception.FriedbergException;

/**
 * Handles the 'alist' command for Friedberg.
 * Displays all tasks in the archive list.
 */
public class ArchiveListCommand implements Command {
    @Override
    public boolean isCommand(String userInput) {
        return ParserUtil.hasCommandWord(userInput, "alist");
    }

    @Override
    public String execute(String userInput, CommandContext commandContext) throws FriedbergException {
        ParserUtil.parseNoArgumentCommand(userInput, "alist");
        return commandContext.renderArchivedTasks();
    }

    @Override
    public boolean isBye() {
        return false;
    }
}
