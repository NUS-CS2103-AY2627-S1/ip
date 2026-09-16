package serangooner.command;

import serangooner.storage.Storage;
import serangooner.task.Task;
import serangooner.task.TaskList;
import serangooner.ui.Ui;

/**
 * Adds a task to the list.
 * Every kind of task is added the same way, so one command serves todos,
 * deadlines and events alike.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Constructs a command that adds the given task.
     *
     * @param task Task to add, already built from what the user typed.
     */
    public AddCommand(Task task) {
        assert task != null : "the parser builds the task before it builds this command";
        this.task = task;
    }

    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        tasks.add(task);
        storage.save(tasks.getTasks());
        return ui.formatTaskAdded(task, tasks.size());
    }
}
