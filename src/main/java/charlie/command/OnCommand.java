package charlie.command;

import java.time.LocalDate;

import charlie.storage.Storage;
import charlie.task.Task;
import charlie.task.TaskList;
import charlie.ui.Ui;

/**
 * Displays deadlines and events occurring on a specified date.
 */
public class OnCommand extends Command {
    /** Date on which matching tasks must occur. */
    private final LocalDate searchDate;

    /**
     * Creates a command for the date to search.
     *
     * @param searchDate Date on which tasks must occur.
     */
    public OnCommand(LocalDate searchDate) {
        this.searchDate = searchDate;
    }

    /**
     * Finds and displays dated tasks occurring on the requested date.
     *
     * @param tasks Task collection to search.
     * @param ui User interface used to display the results.
     * @param storage Storage collaborator, which this read-only command does not use.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMessage("Here's what I found for " + searchDate + ":");
        int matchCount = 0;
        for (Task task : tasks.findOnDate(searchDate)) {
            matchCount++;
            ui.showMessage(matchCount + "." + task);
        }

        if (matchCount == 0) {
            ui.showMessage("Hmm... I couldn't track down anything for this date.");
        }
    }
}
