package dobby.command;

import dobby.logic.DobbyLogic;

/**
 * Adds a new todo task.
 */
public final class TodoCommand extends Command {
    /** Description supplied for the todo. */
    private final String description;

    /** Creates a command with a todo description. */
    public TodoCommand(String description) {
        this.description = description;
    }

    /** Adds the todo task. */
    @Override
    public String execute(DobbyLogic logic) {
        return logic.createToDo(description);
    }
}
