package goop.command;

import java.io.IOException;

import goop.exception.GoopException;
import goop.storage.Storage;
import goop.task.Task;
import goop.task.TaskList;
import goop.ui.Ui;

/**
 * Represents a request to add a parsed task.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates an add command for the given task.
     *
     * @param task Task to add.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds the task, persists the list, and rolls back if saving fails.
     *
     * @param tasks Task list to which the task is added.
     * @param ui User interface used to display the added task.
     * @param storage Storage used to persist the updated task list.
     * @throws IOException If the updated task list cannot be saved.
     * @throws GoopException If a task with the same details already exists.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws IOException, GoopException {
        if (tasks.getTasks().stream().anyMatch(existing -> existing.hasSameDetails(task))) {
            throw new GoopException("This task already exists. Use list to find it.");
        }
        tasks.add(task);
        try {
            storage.saveTasks(tasks);
        } catch (IOException error) {
            assert tasks.size() > 0 && tasks.get(tasks.size() - 1) == task
                    : "The added task must remain last so rollback removes the correct task";
            tasks.delete(tasks.size() - 1);
            throw error;
        }
        ui.showAddedTask(task, tasks.size());
    }
}
