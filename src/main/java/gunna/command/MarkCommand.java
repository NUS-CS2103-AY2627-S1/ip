package gunna.command;

import gunna.DukeException;
import gunna.Storage;
import gunna.TaskList;
import gunna.Ui;
import gunna.task.Task;
/**
 * Represents a command to mark a task as done.
 */
public class MarkCommand extends Command {
    private int taskIndex;

    /**
     * Constructs a MarkCommand with the specified task index.
     *
     * @param taskIndex The 0-based index of the task to mark.
     */
    public MarkCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    /**
     * Executes the mark command by marking the specified task as done.
     *
     * @param tasks The task list containing the task.
     * @param ui The UI to display success message.
     * @param storage The storage to save the updated task list.
     * @throws DukeException If the task index is out of bounds.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws DukeException {
        assert tasks != null : "Task list cannot be null";
        assert ui != null : "UI cannot be null";
        assert storage != null : "Storage cannot be null";
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new DukeException("Task number " + (taskIndex + 1) + " is not in the queue.\n"
                    + "     Queue contains " + tasks.size() + " task(s).");
        }
        tasks.mark(taskIndex);
        Task task = tasks.get(taskIndex);
        ui.showTaskMarked(task);
        storage.saveTasks(tasks.getTasks());
        return "Marked complete:\n  " + task;
    }
}
