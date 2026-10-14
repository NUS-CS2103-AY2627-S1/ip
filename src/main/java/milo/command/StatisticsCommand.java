package milo.command;

import milo.storage.Storage;
import milo.task.TaskList;
import milo.task.TaskStatistics;
import milo.ui.Ui;

/** Displays statistics about the current task list without modifying saved data. */
public class StatisticsCommand extends Command {
    /** Creates a command that summarizes the current task list. */
    public StatisticsCommand() {
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showStatistics(new TaskStatistics(tasks));
    }
}
