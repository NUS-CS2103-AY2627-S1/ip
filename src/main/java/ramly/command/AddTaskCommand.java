package ramly.command;

import ramly.model.Task;
import ramly.model.TaskList;
import ramly.storage.Storage;
import ramly.ui.Ui;

/** Shared workflow for commands that create and save tasks. */
public abstract class AddTaskCommand extends Command {
    /** Creates the concrete task represented by this command. */
    protected abstract Task createTask();

    /** Creates, saves, and reports the newly created task. */
    @Override
    public final void execute(TaskList tasks, Ui ui, Storage storage) {
        Task task = createTask();
        assert task != null : "Add-task commands must create a task";
        tasks.add(task);
        storage.save(tasks);
        ui.show("Trail marker set! I've added this task:",
                " " + task,
                "Now you have " + tasks.size() + " tasks in the list.");
    }
}
