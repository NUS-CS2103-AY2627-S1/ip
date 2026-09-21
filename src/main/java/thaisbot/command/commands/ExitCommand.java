package thaisbot.command.commands;

import thaisbot.Storage;
import thaisbot.ThaisBotException;
import thaisbot.Ui;
import thaisbot.command.Command;
import thaisbot.task.TaskList;

/**
 * Command that exits the application.
 */
public class ExitCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ThaisBotException {
        ui.showBye();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
