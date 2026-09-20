package fifi.command;

import java.time.LocalDate;

import fifi.Storage;
import fifi.TaskList;
import fifi.Ui;

/**
 * Shows tasks that occur on a specific date.
 */
public class ShowCommand extends Command {
    private final LocalDate showDate;

    /**
     * Creates a command that shows tasks occurring on the given date.
     *
     * @param showDate the date to search for
     */
    public ShowCommand(LocalDate showDate) {
        this.showDate = showDate;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        TaskList occurringTasks = tasks.getTasksOccurringOn(showDate);
        ui.showResponse(String.format(
                "Here are the tasks occurring on your specified date:%s",
                occurringTasks.toDisplayString()));
    }
}
