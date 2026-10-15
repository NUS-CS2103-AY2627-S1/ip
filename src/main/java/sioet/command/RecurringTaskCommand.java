package sioet.command;

import sioet.SioetException;
import sioet.storage.Storage;
import sioet.task.RecurringTask;
import sioet.task.Task;
import sioet.task.TaskList;
import sioet.ui.Ui;

/**
 * Represents the command that adds a recurring task.
 */
public class RecurringTaskCommand extends Command {
    private final String taskText;

    /**
     * Creates a recurring task command.
     *
     * @param taskText the recurring task details
     */
    public RecurringTaskCommand(String taskText) {
        this.taskText = taskText;
    }

    /**
     * Executes the recurring task command.
     *
     * @param tasks   the task list
     * @param ui      the user interface
     * @param storage the task storage
     * @throws SioetException if the recurring task format is invalid
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage)
            throws SioetException {
        int markerIndex = taskText.indexOf(" /");

        if (markerIndex < 1
                || markerIndex + 2 >= taskText.length()) {
            throw new SioetException(
                    "use: repeat DESCRIPTION /INTERVAL. "
                            + "Example: repeat project meeting /week");
        }

        String description = taskText.substring(0, markerIndex).trim();
        String recurrence = taskText.substring(markerIndex + 2).trim();

        if (description.isBlank() || recurrence.isBlank()) {
            throw new SioetException(
                    "use: repeat DESCRIPTION /INTERVAL. "
                            + "Example: repeat project meeting /week");
        }

        if (!recurrence.equals("day")
                && !recurrence.equals("week")
                && !recurrence.equals("month")) {
            throw new SioetException(
                    "Recurrence must be day, week, or month.");
        }

        Task task = new RecurringTask(description, recurrence);
        tasks.add(task);
        storage.save(tasks);
        ui.showTaskAdded(task, tasks.size());
    }
}
