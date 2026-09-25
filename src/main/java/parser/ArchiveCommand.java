package parser;

import exception.FriedbergException;
import task.Task;

/**
 * Handles the 'archive' command for Friedberg.
 * Moves a task from the normal task list into the archive list.
 */
public class ArchiveCommand implements Command {
    @Override
    public boolean isCommand(String userInput) {
        return ParserUtil.hasCommandWord(userInput, "archive");
    }

    @Override
    public String execute(String userInput, CommandContext commandContext) throws FriedbergException {
        int taskIndex = ParserUtil.parseIndexCommand(userInput, "archive");
        Task archivedTask = commandContext.archiveTask(taskIndex);
        return String.format("Archived this task:\n%s\nNow you have %d tasks in the list and %d archived tasks.",
                archivedTask.renderTask(), commandContext.getTasksSize(), commandContext.getArchivedTasksSize());
    }

    @Override
    public boolean isBye() {
        return false;
    }
}
