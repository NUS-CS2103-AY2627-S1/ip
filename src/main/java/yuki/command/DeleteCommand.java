package yuki.command;

import java.util.ArrayList;
import java.util.List;

import yuki.storage.Storage;
import yuki.task.Task;
import yuki.task.TaskList;
import yuki.ui.Ui;

/**
 * Deletes a task selected by its user-facing task number.
 */
public class DeleteCommand extends Command {
    /** One-based task number entered by the user. */
    private final int taskNumber;

    /**
     * Creates a command that deletes one task.
     *
     * @param taskNumber One-based task number to delete.
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /** Deletes, saves, and displays the selected task. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        int previousTaskCount = tasks.size();
        Task taskToRemove = tasks.getTask(taskNumber);
        List<Task> updatedTasks = new ArrayList<>(tasks.getTasks());
        updatedTasks.remove(taskNumber - 1);
        storage.saveTasks(updatedTasks);

        Task removedTask = tasks.deleteTask(taskNumber);
        assert removedTask == taskToRemove
                : "The saved and removed tasks must refer to the same task";
        assert tasks.size() == previousTaskCount - 1
                : "Deleting one task must decrease the task count by one";

        ui.showTaskDeleted(removedTask, tasks.size());
    }
}
