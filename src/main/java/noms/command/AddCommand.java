package noms.command;

import noms.exception.DuplicateTaskException;
import noms.exception.NomsException;
import noms.parser.Parser;
import noms.storage.Storage;
import noms.task.Task;
import noms.task.TaskList;
import noms.ui.Ui;

/**
 * Adds a ready-made task (todo, deadline, or event) to the task list.
 * The task is built by the {@link Parser} before this command is created,
 * so executing it simply stores and confirms the task.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that adds the given task.
     *
     * @param task the task to add, already parsed from the user's command
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws NomsException {
        if (tasks.hasMatchingTask(task)) {
            throw new DuplicateTaskException();
        }
        tasks.add(task);
        save(tasks, storage, () -> tasks.delete(tasks.size() - 1));
        ui.showTaskAdded(task, tasks.size());
    }
}
