package summer.command;

import java.time.LocalDate;

import summer.storage.Storage;
import summer.task.TaskList;
import summer.ui.Ui;

/** The "on DATE" command: shows every task that occurs on the given date. */
public class OnDateCommand extends Command {
    private final LocalDate date;

    /**
     * Creates the command.
     *
     * @param date the date to filter tasks by
     */
    public OnDateCommand(LocalDate date) {
        this.date = date;
    }

    /** Prints every stored task that occurs on the stored date. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMessage(tasks.listOn(this.date));
    }
}
