package sage.command;

import java.io.IOException;

import sage.exception.SageException;
import sage.model.TaskList;
import sage.model.Todo;
import sage.storage.Storage;
import sage.ui.Ui;

/** Command that creates and saves a Todo task. */
public class AddTodoCommand extends Command {
    private final String description;

    /**
     * Creates a Todo command with the supplied description.
     *
     * @param description the description of the Todo task
     */
    public AddTodoCommand(String description) {
        this.description = description;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws IOException, SageException {
        if (description.isBlank()) {
            throw new SageException(
                    "Your todo needs a description. What would you like to add?");
        }

        Todo todo = new Todo(description);
        if (tasks.containsEquivalent(todo)) {
            throw new SageException("This task is already in your list.");
        }
        tasks.add(todo);
        storage.save(tasks);

        ui.show("Got it. I've added this task:");
        ui.show("  " + tasks.get(tasks.size() - 1));
        ui.show("Now you have " + tasks.size() + " task"
                + (tasks.size() == 1 ? "" : "s") + " in the list.");
    }
}
