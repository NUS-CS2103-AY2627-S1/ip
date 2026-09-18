package aster.command;

import aster.storage.Storage;
import aster.task.TaskList;
import aster.ui.Ui;

/**
 * Shows statistics about the tasks in the list.
 *
 * <p>Like {@link ListCommand} and {@link FindCommand}, this command only reads the list,
 * so it never saves.
 */
public class StatsCommand extends Command {
    /**
     * Creates a command that shows statistics about the task list.
     */
    public StatsCommand() {
    }

    /**
     * Shows how many tasks there are, how many are done, and how many of each type.
     *
     * @param tasks the task list to summarize.
     * @param ui the user interface to show the statistics through.
     * @param storage the store; unused, because counting changes nothing.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showStatistics(tasks.getStatistics());
    }
}
