package bubu.command;

import java.time.LocalDateTime;
import java.util.List;

import bubu.storage.Storage;
import bubu.task.Task;
import bubu.task.TaskList;
import bubu.ui.Ui;

/** Displays incomplete deadlines and events occurring within the next three days. */
public class RemindCommand extends Command {
    private static final long REMINDER_WINDOW_DAYS = 3;

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime reminderWindowEnd = currentTime.plusDays(REMINDER_WINDOW_DAYS);
        List<Task> upcomingTasks = tasks.findIncompleteTasksDueBetween(currentTime, reminderWindowEnd);
        ui.showUpcomingTasks(upcomingTasks);
    }
}
