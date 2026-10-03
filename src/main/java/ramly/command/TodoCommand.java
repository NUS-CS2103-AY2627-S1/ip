package ramly.command;

import ramly.model.Task;
import ramly.model.Todo;

/** Command that adds a todo task. */
public class TodoCommand extends AddTaskCommand {
    private final String description;
    /** Creates a todo command for the supplied description. */
    public TodoCommand(String description) {
        this.description = description;
    }

    @Override
    protected Task createTask() {
        return new Todo(description);
    }
}
