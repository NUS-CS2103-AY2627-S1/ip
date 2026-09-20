package grower.commands;

import grower.tasks.TaskList;
import grower.ui.Ui;

/**
 * Represents a command that displays all tasks.
 */
public class ListCommand extends Command {
    @Override
    public boolean execute(TaskList tasks, Ui ui) {
        ui.showTaskList(tasks.getTasks());
        return true;
    }
}
