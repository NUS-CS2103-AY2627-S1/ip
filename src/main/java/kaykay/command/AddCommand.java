package kaykay.command;

import java.io.IOException;

import kaykay.model.ApplicationData;
import kaykay.model.Task;
import kaykay.model.TaskList;
import kaykay.storage.Storage;
import kaykay.ui.Ui;

/**
 * Provides shared save-and-confirm behavior for task-creation commands.
 */
public abstract class AddCommand extends Command {
    /** Description shared by the command and the task it creates. */
    private final String description;

    /**
     * Creates an add command for a task description.
     *
     * @param description description of the task to create.
     */
    protected AddCommand(String description) {
        this.description = description;
    }

    /**
     * Creates the concrete task represented by this command.
     *
     * @return the task represented by this command.
     */
    protected abstract Task createTask();

    /**
     * Returns the description supplied to this command.
     *
     * @return the task description.
     */
    protected String getDescription() {
        return description;
    }

    /**
     * Checks whether this command should add its task.
     *
     * @param tasks tasks that may already contain an equivalent entry.
     * @param taskToAdd task that would be added.
     * @param ui UI used to explain why an addition is skipped.
     * @return true when the task should be added.
     */
    private boolean shouldAddTask(TaskList tasks, Task taskToAdd, Ui ui) {
        for (int i = 0; i < tasks.size(); i += 1) {
            Task existingTask = tasks.getTask(i);
            if (existingTask.hasSameDetails(taskToAdd)) {
                ui.showDuplicateTask(existingTask, i + 1);
                return false;
            }
        }
        return true;
    }

    /**
     * Creates, adds, saves, and confirms the task represented by this command.
     * The task is removed again if saving fails.
     *
     * @param data application data containing the task list to modify.
     * @param ui UI used to show the confirmation.
     * @param storage storage used to persist the updated task list.
     * @throws IOException if the updated task list cannot be saved.
     */
    @Override
    public final void execute(ApplicationData data, Ui ui, Storage storage) throws IOException {
        TaskList tasks = data.getTasks();
        Task addedTask = createTask();
        assert addedTask != null : "An add command must create a task";
        if (!shouldAddTask(tasks, addedTask, ui)) {
            return;
        }
        int originalTaskCount = tasks.size();

        tasks.add(addedTask);
        assert tasks.size() == originalTaskCount + 1 : "Adding a task must increase the task count by one";
        try {
            storage.saveData(data);
        } catch (IOException exception) {
            tasks.remove(addedTask);
            assert tasks.size() == originalTaskCount : "A failed save must restore the original task count";
            throw exception;
        }
        showConfirmation(ui, addedTask, tasks.size());
    }

    /** Shows the confirmation appropriate for the task type. */
    protected void showConfirmation(Ui ui, Task addedTask, int taskCount) {
        ui.showAddedTask(addedTask, taskCount);
    }
}
