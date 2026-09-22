package longfrog.command;

import java.time.LocalDate;
import java.util.List;

import longfrog.task.Task;
import longfrog.task.TaskList;
import longfrog.ui.Ui;
import longfrog.util.FormatUtils;

/** Lists deadlines and events that occur on a selected date. */
public class DateCommand implements Command {
    private final TaskList taskList;
    private final LocalDate targetDate;

    /**
     * Creates a date command for the selected date.
     *
     * @param taskList the tasks to inspect
     * @param targetDate the date to search
     */
    public DateCommand(TaskList taskList, LocalDate targetDate) {
        this.taskList = taskList;
        this.targetDate = targetDate;
    }

    /** Finds matching deadlines and events and displays them through the UI. */
    @Override
    public boolean execute(Ui ui) {
        List<Task> matchingTasks = taskList.getTasks().stream()
                .filter(task -> task.occursOn(targetDate))
                .toList();

        String formattedDate = targetDate.format(FormatUtils.DATE_ONLY_FORMAT);
        if (matchingTasks.isEmpty()) {
            ui.showMessage("Temporal query returned zero tasks for " + formattedDate + ".");
        } else {
            ui.showMessage("Temporal query complete for " + formattedDate + ":");
            ui.showNumberedTasks(matchingTasks);
        }

        return false;
    }
}
