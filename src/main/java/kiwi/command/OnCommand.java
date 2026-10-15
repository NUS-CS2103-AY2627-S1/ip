package kiwi.command;

import java.time.LocalDate;

import kiwi.Storage;
import kiwi.Ui;
import kiwi.task.TaskList;

/**
 * Lists deadlines/events that occur on a given date.
 */
public class OnCommand extends Command {
    private final LocalDate date;

    /**
     * Creates a command that lists deadlines and events on the given date.
     *
     * @param date date to filter by.
     */
    public OnCommand(LocalDate date) {
        assert date != null : "Parser already parsed the on-command date";
        this.date = date;
    }

    /**
     * Shows deadlines and events that occur on this command's date.
     *
     * {@inheritDoc}
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTasksOn(date, tasks);
    }
}
