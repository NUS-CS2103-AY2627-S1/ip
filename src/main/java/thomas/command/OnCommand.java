package thomas.command;

import java.time.LocalDate;

import thomas.storage.Storage;
import thomas.task.TaskList;
import thomas.ui.Ui;

/**
 * Shows the tasks falling on one day: the {@code on <day>} command.
 */
public class OnCommand extends Command {
    /** The day being asked about, already read from the typed line. */
    private final LocalDate day;

    /**
     * Creates the command for one day.
     *
     * @param day The day to report on.
     */
    public OnCommand(LocalDate day) {
        this.day = day;
    }

    /**
     * Returns the tasks that fall on this command's day.
     * <p>
     * Nothing is saved, because nothing changed.
     *
     * @param tasks The tasks to search.
     * @param ui Used to word the matches.
     * @param storage Unused.
     * @param history Unused.
     * @return The matches, numbered by list position.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage, History history) {
        return ui.getTasksOnDayMessage(tasks, day);
    }
}
