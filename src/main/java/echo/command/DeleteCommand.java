package echo.command;

import java.util.ArrayList;
import java.util.List;

import echo.exception.EchoException;
import echo.storage.Storage;
import echo.task.Task;
import echo.task.TaskList;
import echo.ui.Ui;

/**
 * Removes one task from the list by its zero-based index.
 */
public class DeleteCommand extends Command {
    private final int index;

    /**
     * Creates a command that deletes one task.
     * See: {@link Command#execute(TaskList, Ui, Storage)}
     * @param index zero-based index of the task to remove
     */
    public DeleteCommand(int index) {
        this.index = index;
    }

    /**
     * Deletes the task, saves the list, and reports which task went away
     * together with the new task count.
     * See: {@link Command#execute(TaskList, Ui, Storage)}
     */
    @Override
    public String execute(TaskList taskList, Ui ui, Storage storage) throws EchoException {
        Task removed = taskList.getTask(index);
        List<Task> proposedTasks = new ArrayList<>(taskList.getAll());
        proposedTasks.remove(index);
        storage.save(proposedTasks);
        taskList.delete(index);
        return formatResponse(taskList, ui, removed);
    }

    private static String formatResponse(TaskList taskList, Ui ui, Task removed) {
        String missionNoun = taskList.size() == 1 ? "mission" : "missions";
        return ui.joinLines("Mission removed from the manifest:",
                removed.toString(),
                "You now have " + taskList.size() + " " + missionNoun + " on the manifest.");
    }
}
