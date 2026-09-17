package lebron.command;

import lebron.exception.LeBronException;
import lebron.task.Task;
import lebron.task.TaskList;
import lebron.task.Todo;
import lebron.ui.Ui;

/**
 * Represents the "todo" command, which adds a Todo to the TaskList.
 */
public class TodoCommand implements Command {
    private final String description;

    /**
     * Constructs a TodoCommand with the given description.
     *
     * @param description The Todo's description.
     */
    public TodoCommand(String description) {
        this.description = description;
    }

    @Override
    public String execute(TaskList taskList, Ui ui) throws LeBronException {
        Task task = new Todo(description);
        taskList.addTask(task);
        return ui.showTodoAdded(task, taskList.size());
    }
}
