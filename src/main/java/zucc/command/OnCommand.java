package zucc.command;

import java.time.LocalDate;

import zucc.ZuccException;
import zucc.storage.Storage;
import zucc.task.TaskDateTimeFormat;
import zucc.task.TaskList;
import zucc.ui.Ui;

/**
 * Displays tasks scheduled on a particular date.
 */
public final class OnCommand extends Command {
    /** Creates a date-query command awaiting values from Parser. */
    OnCommand() {
        super("on");
    }

    /**
     * Finds and displays tasks scheduled on this command's date.
     *
     * @param tasks task collection to search.
     * @param ui user interface through which the result is shown.
     * @param storage persistent storage; unchanged by this command.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ZuccException {
        LocalDate date = TaskDateTimeFormat.parseDate(requireArgument("a date"));
        String displayDate = TaskDateTimeFormat.formatDateForDisplay(date);
        String matchingTasks = tasks.formatTasksOn(date);
        if (matchingTasks.isEmpty()) {
            ui.showMessage("Zucc scanned the timeline and found nothing on "
                    + displayDate + ". Suspiciously peaceful.");
        } else {
            ui.showMessage("Here are the tasks on " + displayDate + ":\n"
                    + matchingTasks);
        }
    }
}
