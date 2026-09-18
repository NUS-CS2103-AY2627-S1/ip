package meka.command;

import java.io.IOException;

import meka.storage.Storage;
import meka.task.Task;
import meka.task.TaskList;
import meka.ui.Ui;

/**
 * Adds a parsed task to the task list and saves the updated list.
 */
public class AddCommand extends Command {
    /** Task created from the user's command. */
    private final Task task;

    /**
     * Creates a command that adds the supplied task.
     *
     * @param task task to add.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds and saves the task, then shows the updated task count.
     *
     * @param tasks task list to modify.
     * @param ui user interface through which confirmation is displayed.
     * @param storage storage component used to persist the change.
     * @throws IOException if the updated task list cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws IOException {
        tasks.add(task);
        storage.save(tasks);
        ui.showTaskAdded(task, tasks.size());
    }
}
