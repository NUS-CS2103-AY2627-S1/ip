package yuki.command;

import yuki.exception.YukiException;
import yuki.storage.Storage;
import yuki.task.Task;
import yuki.task.TaskList;
import yuki.ui.Ui;

/**
 * Adds a task to Yuki's task list.
 */
public class AddCommand extends Command {
    /** Task created by the parser from the user's command. */
    private final Task task;

    /**
     * Creates a command that adds the supplied task.
     *
     * @param task Task to add.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /** Adds, saves, and displays the new task. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        int previousTaskCount = tasks.size();
        tasks.addTask(task);
        assert tasks.size() == previousTaskCount + 1
                : "Adding one task must increase the task count by one";

        try {
            storage.saveTasks(tasks.getTasks());
        } catch (YukiException e) {
            tasks.deleteTask(tasks.size());
            throw e;
        }
        ui.showTaskAdded(task, tasks.size());
    }
}
