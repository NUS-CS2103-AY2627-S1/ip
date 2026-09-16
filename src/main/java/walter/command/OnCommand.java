package walter.command;

import java.time.LocalDate;

import walter.storage.Storage;
import walter.task.TaskList;
import walter.ui.Ui;

/**
 * Displays Deadlines occurring on one date without changing persisted state.
 */
public class OnCommand extends Command {
    private final LocalDate date;

    /**
     * Creates a date lookup command.
     *
     * @param date Date whose deadlines should be displayed.
     */
    public OnCommand(LocalDate date) {
        this.date = date;
    }

    /**
     * Displays deadlines occurring on the selected date without modifying or saving tasks.
     *
     * @param tasks Task list to search for matching deadlines.
     * @param ui User interface used to display the matching deadlines.
     * @param storage Storage component supplied by the application loop; it is not used.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showDeadlinesOn(date, tasks.getDeadlinesOn(date));
    }
}
