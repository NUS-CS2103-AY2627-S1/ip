package chausistant.command;

import java.io.IOException;

import chausistant.exception.ChausistantException;
import chausistant.storage.Storage;
import chausistant.task.Task;
import chausistant.task.TaskList;
import chausistant.ui.Ui;

/** Command that adds one already-validated task to the task list. */
public class AddCommand extends Command {
    private final Task task;

    /** Creates a command that adds the supplied task. */
    public AddCommand(Task task) {
        // The parser always constructs add commands with a successfully created task.
        assert task != null : "Add commands must contain a task.";
        this.task = task;
    }

    /** Adds the task, saves the updated list, and shows a confirmation. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ChausistantException, IOException {
        if (tasks.hasTaskWithSameDetails(task)) {
            throw new ChausistantException("This task is already in your list.");
        }

        int taskCountBefore = tasks.size();
        tasks.add(task);
        assert tasks.size() == taskCountBefore + 1 : "Adding one task must increase the list size by one.";
        try {
            storage.save(tasks);
        } catch (IOException error) {
            Task restoredTask = tasks.remove(tasks.size() - 1);
            assert restoredTask == task : "Saving failure must remove the task that this command added.";
            assert tasks.size() == taskCountBefore : "Saving failure must restore the original task-list size.";
            throw error;
        }
        ui.showTaskAdded(task.printTask(), tasks.size());
    }
}
