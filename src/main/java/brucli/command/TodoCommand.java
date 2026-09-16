package brucli.command;

import java.io.IOException;

import brucli.storage.Storage;
import brucli.task.TaskList;
import brucli.ui.Ui;

/**
 * Adds an undated todo task.
 */
public class TodoCommand extends AddCommand {

    /**
     * Creates a todo command with the supplied description.
     */
    public TodoCommand(String description) {
        super(description);
    }

    /**
     * Adds the todo, saves the list, and displays confirmation.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws IOException {
        tasks.addTodo(description);
        saveTasks(tasks, storage);
        ui.showTodoAdded();
    }
}
