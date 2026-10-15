package pebby.command;

import java.time.LocalDate;
import java.util.List;

import pebby.storage.Storage;
import pebby.task.Deadline;
import pebby.task.Task;
import pebby.task.TaskList;
import pebby.ui.Ui;

/**
 * Displays the incomplete and completed dated tasks that occur on one date.
 */
public class ScheduleCommand extends Command {
    private final String dateText;

    /**
     * Creates a schedule command for the supplied date text.
     */
    public ScheduleCommand(String dateText) {
        this.dateText = dateText;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        try {
            LocalDate date = Deadline.parseDate(dateText);
            List<Task> scheduledTasks = tasks.getScheduleFor(date);
            ui.show(formatSchedule(date, scheduledTasks));
        } catch (IllegalArgumentException exception) {
            ui.showLine("Invalid schedule: " + exception.getMessage());
        }
    }

    /**
     * Formats the schedule or an empty-schedule message for the specified date.
     */
    private String formatSchedule(LocalDate date, List<Task> scheduledTasks) {
        if (scheduledTasks.isEmpty()) {
            return "No tasks scheduled for " + date + ".\n";
        }

        StringBuilder schedule = new StringBuilder("Schedule for ").append(date).append(":\n");
        for (int index = 0; index < scheduledTasks.size(); index++) {
            schedule.append(index + 1).append(". ").append(scheduledTasks.get(index)).append('\n');
        }
        return schedule.toString();
    }
}
