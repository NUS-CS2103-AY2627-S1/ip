package parser;

import exception.FriedbergException;
import task.Task;

/**
 * Handles the 'delete' command for Friedberg.
 * Removes a task from the task list.
 */
public class DeleteCommand implements Command {
    @Override
    public boolean isCommand(String userInput) {
        return ParserUtil.hasCommandWord(userInput, "delete");
    }

    @Override
    public String execute(String userInput, CommandContext commandContext) throws FriedbergException {
        int taskIndex = ParserUtil.parseIndexCommand(userInput, "delete");
        Task removedTask = commandContext.removeTask(taskIndex);
        return String.format("Noted. I've removed this task:\n%s\nNow you have %d tasks in the list.",
                removedTask.renderTask(), commandContext.getTasksSize());
    }

    @Override
    public boolean isBye() {
        return false;
    }
}
