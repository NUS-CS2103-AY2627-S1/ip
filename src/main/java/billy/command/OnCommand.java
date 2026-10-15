package billy.command;

import java.time.LocalDate;
import java.util.List;

import billy.storage.Storage;
import billy.task.TaskDate;
import billy.task.TaskList;
import billy.ui.Ui;

/**
 * Shows the tasks falling on one particular day.
 *
 * <p>Each match keeps the number it has in the full list, so a task found this
 * way can be marked or deleted straight away without running {@code list} first
 * to look its number up.
 */
public class OnCommand extends Command {

    /** The day being asked about. */
    private final LocalDate day;

    /**
     * Creates a command that will look at one day.
     *
     * @param day the day to look at
     */
    public OnCommand(LocalDate day) {
        this.day = day;
    }

    /**
     * Shows the tasks falling on the day being asked about.
     *
     * <p>Each match keeps the number it has in the full list, so a task found
     * this way can be marked or deleted without running {@code list} first to
     * look its number up.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        // Each task decides for itself whether it falls on the day.
        List<String> found = numberMatching(tasks, task -> task.occursOn(day));

        String shownDay = TaskDate.formatDate(day);
        if (found.isEmpty()) {
            ui.show("Nothing on " + shownDay + ". Enjoy the day off!");
            return;
        }
        ui.show("Here's what " + shownDay + " has in store:", String.join("\n", found));
    }
}
