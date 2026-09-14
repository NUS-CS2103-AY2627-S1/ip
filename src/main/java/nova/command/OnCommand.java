package nova.command;

import java.time.LocalDate;

import nova.DateTimes;
import nova.Storage;
import nova.TaskList;
import nova.Ui;

/** Shows the tasks falling on a particular date. */
public class OnCommand extends Command {
    private final LocalDate date;

    /**
     * Creates a command that will list the tasks on the given date.
     *
     * @param date the date to look up
     */
    public OnCommand(LocalDate date) {
        this.date = date;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTasksOn(DateTimes.formatDate(date), tasks.getTasksOn(date));
    }
}
