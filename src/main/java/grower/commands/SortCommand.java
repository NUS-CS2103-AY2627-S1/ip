package grower.commands;

import grower.tasks.TaskList;
import grower.ui.Ui;

/**
 * Sorts the task list by time with dateless tasks first and displays the result.
 */
public class SortCommand extends Command {
    @Override
    public boolean execute(TaskList tasks, Ui ui) {
        tasks.sortByTime();
        ui.showTaskList(tasks.getTasks());
        return true;
    }
}
