package bibi.command;

import java.time.LocalDate;

import bibi.Storage;
import bibi.Ui;
import bibi.task.Task;
import bibi.task.TaskDateTime;
import bibi.task.TaskList;

/**
 * Shows the deadlines due on one date and the events running on it.
 */
public class OnCommand extends Command {
    private final LocalDate queryDate;

    /**
     * Creates a command that will report on the given date.
     *
     * @param queryDate the date being asked about
     */
    public OnCommand(LocalDate queryDate) {
        this.queryDate = queryDate;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        String shownDate = TaskDateTime.formatDate(queryDate);
        boolean hasMatch = false;

        // Matches keep the number they have in the full list, so a task found
        // this way can be marked or removed without listing everything first.
        int taskNumber = 1;
        for (Task task : tasks.getTasks()) {
            if (task.occursOn(queryDate)) {
                if (!hasMatch) {
                    ui.showMessage("Here is what you have on " + shownDate + ":");
                    hasMatch = true;
                }
                ui.showNumberedTask(taskNumber, task);
            }
            taskNumber++;
        }

        if (!hasMatch) {
            ui.showMessage("You have nothing on " + shownDate + ".");
        }
    }
}
