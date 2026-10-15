package noms.command;

import java.time.LocalDate;

import noms.parser.Parser;
import noms.storage.Storage;
import noms.task.TaskList;
import noms.ui.Ui;

/**
 * Shows the deadlines and events that occur on a given date. The date is
 * parsed by the {@link Parser} before this command is created.
 */
public class OnCommand extends Command {
    private final LocalDate date;

    /**
     * Creates a command that lists the tasks occurring on the given date.
     *
     * @param date the date to list tasks for
     */
    public OnCommand(LocalDate date) {
        this.date = date;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTasksOn(date, tasks.tasksOn(date));
    }
}
