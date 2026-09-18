package meowmeow.command;

import meowmeow.storage.Storage;
import meowmeow.task.Task;
import meowmeow.task.TaskList;
import meowmeow.ui.Ui;

/**
 * Adds a task to the list - the shared outcome of the "todo", "deadline"
 * and "event" commands. {@link meowmeow.parser.Parser Parser} has already built the right
 * {@link Task} subclass, so the three commands differ only in how they are
 * parsed, not in what "add" does.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Constructs a command that adds the given task.
     *
     * @param task the task to add, already built by {@link meowmeow.parser.Parser Parser}.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        tasks.add(task);
        storage.save(tasks.asList());
        return ui.showAdded(task, tasks.size());
    }
}
