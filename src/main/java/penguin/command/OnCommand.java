package penguin.command;

import java.time.LocalDate;

import penguin.storage.Storage;
import penguin.task.TaskList;
import penguin.ui.Ui;

/**
 * Represents a command that finds dated tasks.
 */
public class OnCommand extends Command {
    /** Date to search for. */
    private final LocalDate date;

    /**
     * Creates a date-search command.
     *
     * @param searchDate date to search for.
     */
    public OnCommand(LocalDate searchDate) {
        this.date = searchDate;
    }

    /**
     * Displays tasks occurring on the configured date.
     *
     * @param tasks task list to search.
     * @param ui interface used for output.
     * @param storage unused storage.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        assert date != null : "On command must contain a date";
        assert tasks != null : "On command requires a task list";
        assert ui != null : "On command requires a user interface";

        ui.showTasksOnDate(date, tasks.findTasksOnDate(date));
    }
}
