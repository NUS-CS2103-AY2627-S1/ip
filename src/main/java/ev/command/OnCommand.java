package ev.command;

import java.time.LocalDate;

import ev.storage.Storage;
import ev.task.TaskList;
import ev.ui.Ui;

/**
 * Shows the tasks that happen on one date: the deadlines due that day and the events
 * running over it.
 */
public class OnCommand extends Command {

    private final LocalDate date;

    /**
     * Creates a command that will report on the given date.
     *
     * @param date the date the user asked about.
     */
    public OnCommand(LocalDate date) {
        this.date = date;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTasksOn(date, tasks);
    }
}
