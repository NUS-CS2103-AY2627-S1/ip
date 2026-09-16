package chattingheads.command;

import chattingheads.task.Task;
import chattingheads.task.TaskList;
import chattingheads.task.Todo;
import chattingheads.ui.Ui;

/**
 * Represents a command that adds a todo task.
 */
public class AddTodoCommand extends Command {

    private final String description;

    /**
     * Creates a command for adding a todo task.
     *
     * @param description Description of the todo task.
     */
    public AddTodoCommand(String description) {
        this.description = description;
    }

    @Override
    public String execute(TaskList taskList, Ui ui) {
        assert taskList != null : "TaskList should not be null";
        assert ui != null : "Ui should not be null";

        Task newTask = new Todo(description);
        taskList.add(newTask);
        return ui.getAddStatus(newTask, taskList);

    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean shouldSave() {
        return true;
    }
}
