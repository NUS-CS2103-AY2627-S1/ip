package parser;

import exception.FriedbergException;

/**
 * Handles the 'mark' command for Friedberg.
 * Marks a task as done.
 */
public class MarkCommand implements Command {
    @Override
    public boolean isCommand(String userInput) {
        return ParserUtil.hasCommandWord(userInput, "mark");
    }

    @Override
    public String execute(String userInput, CommandContext commandContext) throws FriedbergException {
        int taskIndex = ParserUtil.parseIndexCommand(userInput, "mark");
        commandContext.markTask(taskIndex);
        return String.format("Nice! I've marked this task as done:\n%s", commandContext.renderTask(taskIndex));
    }

    @Override
    public boolean isBye() {
        return false;
    }
}
