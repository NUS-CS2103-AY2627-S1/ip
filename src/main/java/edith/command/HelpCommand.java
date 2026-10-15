package edith.command;

import edith.task.TaskList;
import edith.ui.Ui;

/** Displays the formats of all available commands. */
public class HelpCommand extends Command {
    /** Displays help without modifying the task list. */
    @Override
    public void execute(TaskList tasks, Ui ui) {
        ui.showHelp();
    }
}
