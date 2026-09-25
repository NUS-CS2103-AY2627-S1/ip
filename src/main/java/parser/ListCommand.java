package parser;

import exception.FriedbergException;

/**
 * Handles the 'list' command for Friedberg.
 * Displays all tasks in the task list.
 */
public class ListCommand implements Command {
    @Override
    public boolean isCommand(String userInput) {
        return ParserUtil.hasCommandWord(userInput, "list");
    }

    @Override
    public String execute(String userInput, CommandContext commandContext) throws FriedbergException {
        ParserUtil.parseNoArgumentCommand(userInput, "list");
        return commandContext.renderTasks();
    }

    @Override
    public boolean isBye() {
        return false;
    }
}
