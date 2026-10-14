package bingus.command;

import java.time.LocalDate;

import bingus.exception.BingusException;
import bingus.storage.Storage;
import bingus.task.TaskList;
import bingus.ui.Ui;

/**
 * Displays all tasks, or only tasks related to a specified date.
 */
public class ListCommand extends Command {

    private final LocalDate date;

    /**
     * Creates a command that displays every task.
     */
    public ListCommand() {
        this.date = null;
    }

    /**
     * Creates a command that displays tasks associated with a date.
     *
     * @param date Date used to filter tasks.
     */
    public ListCommand(LocalDate date) {
        this.date = date;
    }

    @Override
    public CommandType getType() {
        return CommandType.LIST;
    }

    /**
     * Returns the complete or date-filtered task list.
     *
     * @param tasks task list to display.
     * @param ui user interface used to format the list.
     * @param storage persistent task storage, which is not changed.
     * @return task-list message.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws BingusException {
        if (date == null) {
            return ui.getTaskListMessage(tasks);
        }
        return ui.getFilteredTaskListMessage(tasks, date);
    }
}
