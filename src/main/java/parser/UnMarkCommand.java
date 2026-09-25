package parser;

import exception.FriedbergException;

/**
 * Handles the 'unmark' command for Friedberg.
 * Marks a task as not done.
 */
public class UnMarkCommand implements Command {
    @Override
    public boolean isCommand(String userInput) {
        return ParserUtil.hasCommandWord(userInput, "unmark");
    }

    @Override
    public String execute(String userInput, CommandContext commandContext) throws FriedbergException {
        int taskIndex = ParserUtil.parseIndexCommand(userInput, "unmark");
        commandContext.unmarkTask(taskIndex);
        return String.format("OK, I've marked this task as not done yet:\n%s", commandContext.renderTask(taskIndex));
    }

    @Override
    public boolean isBye() {
        return false;
    }
}
