package potatobot.logic.command;

import potatobot.logic.CommandResult;
import potatobot.model.task.TaskList;
import potatobot.storage.Storage;

/**
 * Displays tasks whose descriptions contain a specified keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that searches for the specified keyword.
     *
     * @param keyword Keyword to search for in task descriptions.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Displays the tasks matching this command's keyword.
     *
     * @param tasks Task list to search.
     * @param storage Storage used by the application; not needed for searching.
     * @return Result containing matching tasks.
     */
    @Override
    public CommandResult execute(TaskList tasks, Storage storage) {
        return CommandResult.reply(tasks.find(keyword));
    }
}
