package chausistant.command;

import java.io.IOException;

import chausistant.exception.ChausistantException;
import chausistant.storage.Storage;
import chausistant.task.Task;
import chausistant.task.TaskList;
import chausistant.ui.Ui;

/** Command that removes one task from the task list. */
public class DeleteCommand extends NumberedTaskCommand {

    /** Creates a command for the task number supplied after {@code delete}. */
    public DeleteCommand(String taskNumberText) {
        super("delete", taskNumberText);
    }

    /** Removes the selected task, saves the updated list, and shows a confirmation. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage)
            throws ChausistantException, IOException {
        int taskIndex = getTaskIndex(tasks);
        int taskCountBefore = tasks.size();
        Task removedTask = tasks.remove(taskIndex);
        assert tasks.size() == taskCountBefore - 1
                : "Deleting one task must decrease the list size by one.";
        try {
            storage.save(tasks);
        } catch (IOException error) {
            tasks.add(taskIndex, removedTask);
            assert tasks.size() == taskCountBefore
                    : "Saving failure must restore the original task-list size.";
            assert tasks.get(taskIndex) == removedTask
                    : "Saving failure must restore the removed task at its original position.";
            throw error;
        }
        ui.showTaskDeleted(removedTask.printTask(), tasks.size());
    }
}
