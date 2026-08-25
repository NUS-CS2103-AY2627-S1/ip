package matthew.command;

import java.time.LocalDate;

import matthew.storage.Storage;
import matthew.task.TaskList;
import matthew.ui.Ui;

/** Displays deadlines and events occurring on a specified date. */
public class OnCommand extends Command {
    private final LocalDate date;

    /**
     * Creates a command that finds tasks occurring on the specified date.
     *
     * @param date Date on which to search for tasks.
     */
    public OnCommand(LocalDate date) {
        this.date = date;
    }

    /**
     * Finds and displays tasks occurring on the command's date.
     *
     * @param tasks Task list to search.
     * @param ui User interface used to display the matching tasks.
     * @param storage Unused storage supplied by the command loop.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTasksOn(date.toString(), tasks.getTasksOn(date));
    }
}
