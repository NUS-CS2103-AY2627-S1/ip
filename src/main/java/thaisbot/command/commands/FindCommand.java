package thaisbot.command.commands;

import thaisbot.Storage;
import thaisbot.ThaisBotException;
import thaisbot.Ui;
import thaisbot.command.Command;
import thaisbot.task.TaskList;

/**
 * Command that finds tasks by keyword in their descriptions.
 */
public class FindCommand extends Command {
    private final String query;

    /**
     * Construct a FindCommand.
     * @param query keyword to search for
     */
    public FindCommand(String query) {
        this.query = query;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ThaisBotException {
        ui.showMatchingTasks(tasks, query);
    }
}
