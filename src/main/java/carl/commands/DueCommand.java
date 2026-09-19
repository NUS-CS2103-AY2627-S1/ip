package carl.commands;

import java.time.LocalDate;

import carl.exceptions.CarlException;
import carl.task.TaskList;
import carl.task.TaskManager;
import carl.ui.Ui;
/**
 * Represents a command to find and list tasks due on a specific date.
 */
public class DueCommand implements Command {
    private final LocalDate date;

    /**
     * Creates a command that lists tasks occurring on the specified date.
     *
     * @param date date to query.
     */
    public DueCommand(LocalDate date) {
        this.date = date;
    }

    /**
     * {@inheritDoc}
     * Lists all tasks due on the specified date, or today's date if no date is provided.
     *
     * @param ui      User interface for interacting with the user.
     * @param storage Task manager handling task data persistence.
     * @param tasks   List of current tasks.
     * @param raw     Raw input string entered by the user.
     * @return result containing tasks occurring on the requested date.
     * @throws CarlException If the provided date format is invalid.
     */
    @Override
    public CommandResult onRun(Ui ui, TaskManager storage, TaskList tasks, String raw) throws CarlException {
        return CommandResult.createSuccess(ui.showTaskList(tasks.getTasksDueOn(date)));
    }
}
