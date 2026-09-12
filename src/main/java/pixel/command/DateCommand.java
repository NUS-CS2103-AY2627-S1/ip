package pixel.command;

import java.time.LocalDate;

import pixel.storage.Storage;
import pixel.task.Deadline;
import pixel.task.TaskList;
import pixel.ui.Ui;

/**
 * Displays tasks that occur on a specific date.
 */
public class DateCommand extends Command {
    private final LocalDate date;

    /**
     * Creates a query for tasks occurring on the specified date.
     *
     * @param date Date to search for.
     */
    public DateCommand(LocalDate date) {
        this.date = date;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        boolean hasMatchingTask = false;
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).occursOn(date)) {
                if (!hasMatchingTask) {
                    ui.showMessage("Here are the tasks occurring on "
                            + Deadline.formatDate(date) + ":");
                }
                ui.showMessage((i + 1) + "." + tasks.get(i));
                hasMatchingTask = true;
            }
        }
        if (!hasMatchingTask) {
            ui.showMessage("There are no tasks occurring on "
                    + Deadline.formatDate(date) + ".");
        }
    }
}
