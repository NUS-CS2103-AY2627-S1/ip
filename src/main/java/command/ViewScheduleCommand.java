package puyo.command;

import java.time.LocalDate;
import java.util.ArrayList;

import puyo.storage.Storage;
import puyo.task.Task;
import puyo.task.TaskList;
import puyo.ui.Ui;

/**
 * Represents a command to view schedules or tasks for a specific date.
 */
public class ViewScheduleCommand extends Command {

    private final LocalDate date;

    /**
     * Constructs a {@code ViewScheduleCommand} with the specified date.
     *
     * @param date The date to filter tasks by.
     */
    public ViewScheduleCommand(LocalDate date) {
        this.date = date;
    }

    /**
     * Displays tasks scheduled on the requested date in their existing list order.
     * Date filtering was updated with assistance from ChatGPT.
     *
     * @param tasks The list of tasks to search.
     * @param ui The user interface for displaying matches.
     * @param storage The storage handler, unused by this read-only command.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ArrayList<Task> matchingTasks = new ArrayList<>();

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            if (task.isScheduledOn(date)) {
                matchingTasks.add(task);
            }
        }

        if (matchingTasks.isEmpty()) {
            ui.showMessage("No schedules found for date: " + date);
        } else {
            StringBuilder sb = new StringBuilder("Here are the schedules for " + date + ":\n");
            for (int i = 0; i < matchingTasks.size(); i++) {
                sb.append((i + 1)).append(". ").append(matchingTasks.get(i)).append("\n");
            }
            ui.showMessage(sb.toString().trim());
        }
    }
}
