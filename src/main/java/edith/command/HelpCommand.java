package edith.command;

import edith.Storage;
import edith.Ui;
import edith.task.TaskList;

/** Displays the syntax of every available command. */
public class HelpCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showHelp();
    }
}
