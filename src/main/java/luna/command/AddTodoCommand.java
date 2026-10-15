package luna.command;

import luna.exception.LunaException;
import luna.parser.Parser;
import luna.storage.Storage;
import luna.task.Task;
import luna.task.TaskList;
import luna.task.Todo;

/**
 * Adds a todo task.
 */
public class AddTodoCommand extends AddCommand {
    private final String commandArgs;

    /**
     * Creates a command that adds a todo.
     *
     * @param commandArgs User input after the command word.
     * @param tasks Task list to update.
     * @param storage Storage to update.
     */
    public AddTodoCommand(String commandArgs, TaskList tasks, Storage storage) {
        super(tasks, storage);
        this.commandArgs = commandArgs;
    }

    @Override
    protected Task createTask() throws LunaException {
        String desc = Parser.parseTodo(commandArgs); // [desc]
        return new Todo(desc);
    }
}
