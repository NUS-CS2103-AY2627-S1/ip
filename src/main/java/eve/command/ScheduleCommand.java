package eve.command;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import eve.Storage;
import eve.Ui;
import eve.task.Task;
import eve.task.TaskList;

/**
 * Shows either the whole schedule (every deadline and event, in
 * chronological order) or just one date's, depending on whether a date was
 * given. The by-date case shares its display with {@link OnCommand} --
 * "schedule on 2019-12-02" and "on 2019-12-02" show the exact same thing --
 * since a single date's schedule and "what's on that date" are the same
 * question asked two ways.
 */
public class ScheduleCommand extends Command {
    private final Optional<LocalDate> date;

    /**
     * Creates a command that shows the whole schedule, or just one date's.
     *
     * @param date the date to filter to, or empty for the whole schedule.
     */
    public ScheduleCommand(Optional<LocalDate> date) {
        this.date = date;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        if (date.isPresent()) {
            List<Task> matches = tasks.occurringOn(date.get());
            ui.showTasksOnDate(date.get(), matches);
        } else {
            ui.showSchedule(tasks.schedule());
        }
    }
}
