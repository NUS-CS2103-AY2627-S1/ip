package dennis.command;

import java.time.LocalDate;

import dennis.storage.Storage;
import dennis.task.Task;
import dennis.task.TaskList;
import dennis.ui.Ui;

/**
 * Lists the deadlines and events that fall on a given date.
 *
 * <p>Each match is shown at its real position in the list (so the number
 * still works with {@code mark}/{@code unmark}/{@code delete}); this means
 * the numbers shown can skip over tasks that do not match.</p>
 */
public class OnCommand extends Command {
    /** The date to look for. */
    private final LocalDate date;

    /**
     * Creates a command that lists the tasks falling on the given date.
     *
     * @param date the date whose tasks should be listed
     */
    public OnCommand(LocalDate date) {
        this.date = date;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMessage("Here's what's happening on " + Task.formatDate(date)
                + ", if you must know.");

        boolean hasMatch = false;
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.occursOn(date)) {
                hasMatch = true;
                ui.showMessage((i + 1) + "." + task);
            }
        }

        if (!hasMatch) {
            ui.showMessage("You have no deadlines or events on that date.");
        }
    }
}
