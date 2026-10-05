package gatsby.command;

import gatsby.exception.GatsbyException;
import gatsby.model.Task;
import gatsby.model.TaskList;
import gatsby.model.Todo;
import gatsby.ui.Ui;

/**
 * Creates and saves a plain todo task.
 */
public class TodoCommand extends Command {
    /** The description entered after the {@code todo} command. */
    private final String payload;

    /**
     * Creates a todo command for the supplied description.
     *
     * @param payload the text entered after {@code todo}
     */
    public TodoCommand(String payload) {
        this.payload = payload == null ? "" : payload.strip();
    }

    /**
     * Validates, creates, saves, and reports the new todo.
     *
     * @param tasks the current task list
     * @param ui the console interaction handler
     * @throws GatsbyException when the todo description is empty or invalid
     */
    @Override
    public void execute(TaskList tasks, Ui ui) throws GatsbyException {
        Task task = new Todo(requireText(payload,
                " son the description of a todo cannot be empty -_-!"));
        addTask(tasks, ui, task);
    }
}
