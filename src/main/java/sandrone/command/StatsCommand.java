package sandrone.command;

import sandrone.storage.Storage;
import sandrone.task.TaskList;
import sandrone.ui.Ui;

/** Represents the command that displays task statistics. */
public class StatsCommand extends Command {
    /** Displays task statistics calculated from the current task list. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskStatistics(tasks.getStatistics());
    }
}
