package plana.command;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import plana.storage.Storage;
import plana.task.Deadline;
import plana.task.Event;
import plana.task.Task;
import plana.task.TaskList;
import plana.ui.Ui;

/**
 * Displays deadlines and events that occur on a specified date.
 */
public class OnCommand extends Command {
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);
    private final LocalDate date;

    /**
     * Creates a date query command.
     *
     * @param date the date to query.
     */
    public OnCommand(LocalDate date) {
        this.date = date;
    }

    /**
     * Displays matching deadlines and events, or an empty-result message.
     *
     * @param tasks the task list to search.
     * @param ui the user interface used for the query response.
     * @param storage unused by this command.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        String displayDate = date.format(DISPLAY_DATE_FORMAT);
        boolean hasMatchingTask = false;
        ui.showTasksOnDateHeader(displayDate);
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            boolean matches = (task instanceof Deadline deadline && deadline.isOn(date))
                    || (task instanceof Event event && event.occursOn(date));
            if (matches) {
                ui.showTask(i, task);
                hasMatchingTask = true;
            }
        }
        if (!hasMatchingTask) {
            ui.showNoTasksOnDate(displayDate);
        }
        ui.showLine();
    }
}
