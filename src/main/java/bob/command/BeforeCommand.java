package bob.command;

import java.time.LocalDate;

import bob.storage.Storage;
import bob.task.TaskDateTime;
import bob.task.TaskList;
import bob.ui.Ui;

/**
 * Prints the tasks falling before one day, in the order they appear in the list.
 *
 * <p>The named day itself is not included, so {@code before} and {@code on} for the
 * same day never show the same deadline twice. Someone wanting both can ask for the
 * day after.
 *
 * <p>An event can be shown by both, and that is not a contradiction. A task is
 * placed here by the one date it is pinned to, which for an event is its start, while
 * {@link OnCommand} asks which days an event is running on. An event that began on
 * the 1st and runs to the 3rd therefore comes before the 2nd and is also on it.
 */
public class BeforeCommand extends Command {

    /** The day the user asked about. */
    private final LocalDate day;

    /**
     * Creates a command that will list the tasks falling before one day.
     *
     * @param day the day to look before.
     */
    public BeforeCommand(LocalDate day) {
        this.day = day;
    }

    /**
     * Prints the tasks falling before the day this command was built with, or says
     * that there are none.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        String dayShown = TaskDateTime.formatDay(day);
        ui.showTasks(tasks, tasks.findIndexes(task -> task.isBefore(day)),
                "Bob look before " + dayShown + ". Yu have:",
                "Nothing before " + dayShown + ". Yay!");
    }
}
