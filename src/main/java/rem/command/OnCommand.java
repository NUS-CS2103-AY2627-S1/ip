package rem.command;

import java.time.LocalDate;
import java.util.List;

import rem.storage.Storage;
import rem.task.Task;
import rem.task.TaskDateTime;
import rem.task.TaskList;
import rem.ui.Ui;

/**
 * Displays tasks scheduled on a particular date.
 */
public class OnCommand extends Command {
    private final LocalDate date;

    /**
     * Creates a command that searches a date.
     *
     * @param date Date to search for.
     */
    public OnCommand(LocalDate date) {
        this.date = date;
    }

    /**
     * Displays the tasks scheduled on this command's date.
     *
     * @param tasks Tasks managed by Rem.
     * @param ui User interface used to display the results.
     * @param storage Storage available to commands that need persistence.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        List<Task> scheduledTasks = tasks.findTasksOn(date);
        if (scheduledTasks.isEmpty()) {
            ui.showMessage("Nothing scheduled on " + TaskDateTime.format(date) + ". Maybe nap time?");
            return;
        }

        ui.showMessage("Here's what's scheduled on " + TaskDateTime.format(date) + ":");
        ui.showNumberedTasks(scheduledTasks);
    }
}
