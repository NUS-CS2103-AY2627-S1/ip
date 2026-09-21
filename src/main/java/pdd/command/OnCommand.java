package pdd.command;

import java.time.LocalDate;

import pdd.storage.Storage;
import pdd.task.TaskList;
import pdd.ui.Ui;

/** Lists the tasks that occur on an already-parsed date. */
public class OnCommand extends Command {
    private final LocalDate date;

    /** Creates a command that will list the tasks occurring on the given already-parsed date. */
    public OnCommand(LocalDate date) {
        this.date = date;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTasksOn(date, tasks.getTasks());
    }
}
