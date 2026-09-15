package luigibot.command;

import java.time.LocalDate;
import java.util.List;

import luigibot.storage.Storage;
import luigibot.task.TaskList;
import luigibot.ui.Ui;

/**
 * Displays tasks that occur on a specified date.
 */
public class FindDateCommand extends Command {
    private final LocalDate date;

    /**
     * Creates a command that finds tasks occurring on the given date.
     *
     * @param date date to search.
     */
    public FindDateCommand(LocalDate date) {
        this.date = date;
    }

    /**
     * Finds and displays tasks that occur on the stored date.
     *
     * @param tasks stored tasks.
     * @param ui user interface used to display matching tasks.
     * @param storage storage dependency required by the common command interface.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        List<Integer> matchingIndexes = tasks.findIndexesOnDate(this.date);
        ui.showTasksOnDate(this.date, tasks, matchingIndexes);
    }
}
