package benjamin.command;

import java.time.LocalDate;

import benjamin.storage.Storage;
import benjamin.task.TaskList;
import benjamin.ui.Ui;

/** Shows the tasks that fall on one particular date. */
public class OnCommand extends Command {
    private final LocalDate date;

    /**
     * Creates a command that will report the tasks on the given day.
     *
     * @param date the day the user asked about.
     */
    public OnCommand(LocalDate date) {
        this.date = date;
    }

    /** Shows the matching tasks. Nothing is changed, so nothing is saved. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTasksOn(date, tasks.findOn(date));
    }
}
