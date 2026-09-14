package arrodes.command;

import arrodes.storage.Storage;
import arrodes.task.TaskList;
import arrodes.task.Todo;
import arrodes.ui.Ui;

/** Command that adds a todo task and saves the updated list. */
public class TodoCommand extends Command {
    /** Text describing the todo. */
    private final String description;

    /**
     * Creates a command for adding a todo.
     * @param description task description
     */
    public TodoCommand(String description) {
        assert description != null : "Todo description must not be null.";
        this.description = description;
    }

    /**
     * Adds the todo, persists it, and reports the new task count.
     * @param ui interface used for output
     * @param taskList list to update
     * @param storage persistence layer
     */
    @Override
    public void execute(Ui ui, TaskList taskList, Storage storage) {
        addTaskAndSave(new Todo(description), ui, taskList, storage);
    }
}
