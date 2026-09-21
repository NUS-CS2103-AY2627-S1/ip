package echo.command;

import java.util.ArrayList;
import java.util.List;

import echo.exception.DuplicateTaskException;
import echo.exception.EchoException;
import echo.storage.Storage;
import echo.task.Task;
import echo.task.TaskList;
import echo.ui.Ui;

/**
 * Adds one parsed task to the list and confirms the new task count.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that appends the given task.
     *
     * @param task validated task to add
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Appends the task, saves the list, and confirms the addition.
     * See: {@link Command#execute(TaskList, Ui, Storage)}
     */
    @Override
    public String execute(TaskList taskList, Ui ui, Storage storage) throws EchoException {
        if (taskList.containsTaskWithSameDetails(task)) {
            throw new DuplicateTaskException();
        }
        List<Task> proposedTasks = new ArrayList<>(taskList.getAll());
        proposedTasks.add(task);
        storage.save(proposedTasks);
        taskList.add(task);
        String missionNoun = taskList.size() == 1 ? "mission" : "missions";
        return ui.joinLines("Mission logged:",
                task.toString(),
                "You now have " + taskList.size() + " " + missionNoun + " on the manifest.");
    }
}
