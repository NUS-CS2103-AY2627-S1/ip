package lebronjames.command;

import java.time.LocalDate;

import lebronjames.storage.Storage;
import lebronjames.task.TaskList;
import lebronjames.ui.Ui;

/**
 * Shows the tasks scheduled on one particular date.
 */
public class OnCommand extends Command {
    private final LocalDate date;

    /**
     * Creates a command that reports what is scheduled on the given date.
     *
     * @param date Date the user asked about.
     */
    public OnCommand(LocalDate date) {
        this.date = date;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTasksOn(tasks.findTasksOn(date), date);
    }
}
