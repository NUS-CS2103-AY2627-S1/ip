package eve.command;

import java.time.LocalDate;
import java.util.List;

import eve.Storage;
import eve.Ui;
import eve.task.Task;
import eve.task.TaskList;

/** Shows the tasks that occur on a given date. */
public class OnCommand extends Command {
    private final LocalDate date;

    /**
     * Creates a command that shows the tasks occurring on the given date.
     *
     * @param date the date to query.
     */
    public OnCommand(LocalDate date) {
        this.date = date;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        List<Task> matches = tasks.occurringOn(date);
        ui.showTasksOnDate(date, matches);
    }
}
