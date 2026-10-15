package zen.command;

import zen.ZenException;
import zen.storage.Storage;
import zen.task.TaskList;
import zen.ui.Ui;

/** Adds a todo task. */
public class TodoCommand extends AddCommand {
    private final String description;

    /**
     * Creates a command using the supplied todo description.
     *
     * @param description description of the todo to add
     */
    public TodoCommand(String description) {
        this.description = description;
    }

    /** Validates the description and adds the requested todo. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ZenException {
        addTask(Parser.parseTodo(description), tasks, ui, storage);
    }
}
