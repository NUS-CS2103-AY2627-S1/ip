package parser;

import exception.FriedbergException;
import task.Task;

/**
 * Handles the 'unarchive' command for Friedberg.
 * Moves a task from the archive list back into the normal task list.
 */
public class UnarchiveCommand implements Command {
    @Override
    public boolean isCommand(String userInput) {
        return ParserUtil.hasCommandWord(userInput, "unarchive");
    }

    @Override
    public String execute(String userInput, CommandContext commandContext) throws FriedbergException {
        int taskIndex = ParserUtil.parseIndexCommand(userInput, "unarchive");
        Task unarchivedTask = commandContext.unarchiveTask(taskIndex);
        return String.format("Unarchived this task:\n%s\nNow you have %d tasks in the list and %d archived tasks.",
                unarchivedTask.renderTask(), commandContext.getTasksSize(), commandContext.getArchivedTasksSize());
    }

    @Override
    public boolean isBye() {
        return false;
    }
}
