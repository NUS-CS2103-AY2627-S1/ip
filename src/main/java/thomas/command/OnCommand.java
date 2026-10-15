package thomas.command;

import java.time.LocalDate;

import thomas.ThomasException;
import thomas.storage.Storage;
import thomas.task.TaskList;
import thomas.ui.Ui;
import thomas.util.DateTimeUtil;

/**
 * Displays tasks that occur on a given date.
 */
public class OnCommand extends Command {
    private final String dateInput;

    /**
     * Creates a command that lists tasks occurring on a date.
     *
     * @param dateInput Date entered by the user.
     */
    public OnCommand(String dateInput) {
        this.dateInput = dateInput;
    }

    /**
     * Parses the date and prints matching deadlines and events.
     *
     * @param tasks task list to search
     * @param ui user interface used for output
     * @param storage unused for this command
     * @throws ThomasException if the date is missing or cannot be parsed
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ThomasException {
        if (dateInput.isEmpty()) {
            throw new ThomasException("Please provide a date. E.g., on 2026-08-09");
        }
        LocalDate targetDate = DateTimeUtil.parseDate(dateInput);
        ui.showTasksOn(tasks, targetDate);
    }
}
