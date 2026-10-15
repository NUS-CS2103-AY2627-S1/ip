package stephen.command;

import java.time.LocalDate;

import stephen.storage.Storage;
import stephen.task.TaskList;
import stephen.ui.Ui;

/** Displays tasks occurring on one date. */
public class ScheduleCommand extends Command {
    private final LocalDate date;

    /**
     * Creates a command that displays the schedule for the given date.
     *
     * @param date date whose scheduled tasks should be displayed
     */
    public ScheduleCommand(LocalDate date) {
        assert date != null : "A schedule command must contain a parsed date";
        this.date = date;
    }

    /**
     * Displays all tasks that occur on the configured date.
     *
     * @param tasks task list to search
     * @param ui console UI used to display the schedule
     * @param storage persistence service; not used by this command
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showSchedule(tasks, date);
    }
}
