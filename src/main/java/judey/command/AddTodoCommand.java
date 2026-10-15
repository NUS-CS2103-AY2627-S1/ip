package judey.command;

import judey.exception.JudeyException;
import judey.storage.Storage;
import judey.task.Task;
import judey.task.Todo;
import judey.tasklist.TaskList;
import judey.ui.Ui;

/**
 * Represents the action of adding a Todo task
 */
public class AddTodoCommand extends Command {
    private final String description;

    /**
     * Create a Todo task
     * @param description represents what the task is about
     */
    public AddTodoCommand(String description) {
        this.description = description;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws JudeyException {
        Task task = new Todo(description);
        tasks.add(task);
        storage.save(tasks.getTasks());
        ui.showTaskAdded(task, tasks.getSize());
    }
}
