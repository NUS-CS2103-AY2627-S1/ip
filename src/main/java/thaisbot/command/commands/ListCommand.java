package thaisbot.command.commands;

import thaisbot.Storage;
import thaisbot.ThaisBotException;
import thaisbot.Ui;
import thaisbot.command.Command;
import thaisbot.task.TaskList;

/**
 * Command to display the current task list.
 */
public class ListCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ThaisBotException {
        ui.showTaskList(tasks);
    }
}
