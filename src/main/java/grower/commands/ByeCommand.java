package grower.commands;

import grower.tasks.TaskList;
import grower.ui.Ui;

/**
 * Represents a command that ends the application.
 */
public class ByeCommand extends Command {
    @Override
    public boolean execute(TaskList tasks, Ui ui) {
        ui.showGoodbye();
        return false;
    }
}
