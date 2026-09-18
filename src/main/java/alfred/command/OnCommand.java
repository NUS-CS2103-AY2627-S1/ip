package alfred.command;

import java.time.LocalDate;

import alfred.Storage;
import alfred.Ui;
import alfred.task.TaskList;

/**
 * Shows the tasks that fall on one day.
 */
public class OnCommand extends Command {
    private final LocalDate date;

    /**
     * Creates a command that will show one day's tasks.
     *
     * @param date the day being asked about.
     */
    public OnCommand(LocalDate date) {
        this.date = date;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTasksOn(tasks, date);
    }
}
