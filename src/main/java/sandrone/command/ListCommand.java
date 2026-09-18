package sandrone.command;

import java.time.LocalDate;

import sandrone.storage.Storage;
import sandrone.task.TaskList;
import sandrone.ui.Ui;

/**
 * Displays all tasks or tasks on one date.
 */
public class ListCommand extends Command {
    private final LocalDate date;
    private final String dateText;
    /**
     * Creates a command to display every task or tasks for one date.
     */
    public ListCommand(LocalDate date, String dateText) {
        this.date = date;
        this.dateText = dateText;
    }

    /**
     * Displays the requested tasks without changing saved data.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks, date, dateText);
    }
}
