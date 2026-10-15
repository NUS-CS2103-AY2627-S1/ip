package chione.command;

import java.time.LocalDate;

import chione.DateTimes;
import chione.Storage;
import chione.Ui;
import chione.task.TaskList;

/**
 * Shows the tasks falling on one particular day.
 *
 * <p>Like {@link ListCommand} this only reads the list, so nothing is saved.
 */
public class OnCommand extends Command {
    /** The day being asked about. */
    private final LocalDate date;

    /**
     * Remembers the day to look at.
     *
     * @param date the day being asked about
     */
    public OnCommand(LocalDate date) {
        this.date = date;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTasksOn(tasks.findOn(date), DateTimes.formatDate(date));
    }
}
