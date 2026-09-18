package chud.command;

import chud.exception.ChudException;
import chud.parser.Parser;
import chud.storage.Storage;
import chud.task.TaskList;
import chud.task.Todo;
import chud.ui.Ui;

/** Adds a Todo task with the given (not yet validated) description text. */
public class AddTodoCommand extends Command {
    private final String arguments;

    /** Creates a command that will validate and add a todo with the given raw description text. */
    public AddTodoCommand(String arguments) {
        this.arguments = arguments;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ChudException {
        String description = Parser.parseTodoDescription(arguments);
        addTask(tasks, ui, storage, new Todo(description));
    }
}
