package pulbot.command;

import pulbot.PulbotException;
import pulbot.storage.Storage;
import pulbot.task.Task;
import pulbot.task.TaskList;
import pulbot.ui.Ui;

/** Adds a newly created task to the task list. */
public class AddCommand extends Command {
    private final Task task;

    public AddCommand(Task task) {
        this.task = task;
    }

    /** Adds and saves the task, restoring the list if saving fails. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws PulbotException {
        if (tasks.containsDuplicate(task)) {
            throw new PulbotException("This task already exists in your list.");
        }
        tasks.add(task);
        try {
            storage.save(tasks);
        } catch (PulbotException e) {
            tasks.remove(tasks.size() - 1);
            throw e;
        }
        ui.showAddedTask(task, tasks.size());
    }
}
