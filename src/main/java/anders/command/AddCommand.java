package anders.command;

import anders.AndersException;
import anders.collection.TaskList;
import anders.storage.Storage;
import anders.task.Task;
import anders.ui.Ui;

/** Adds a newly parsed task to the task list. */
public class AddCommand extends Command {
    private final Task task;

    /** Creates an add command for the supplied task. */
    public AddCommand(Task task) {
        this.task = task;
    }
    /** Adds the task, saves the updated list, and shows a confirmation. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws AndersException {
        tasks.add(task);
        storage.save(tasks);
        ui.showTaskAdded(task, tasks.size());
    }
}
