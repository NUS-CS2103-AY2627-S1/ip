package zaru.command;

import zaru.exception.ZaruException;
import zaru.task.TaskList;
import zaru.task.ToDo;

/** Executes the {@code todo} command. */
public class TodoCommand extends Command {
    private final String description;

    /**
     * Creates a todo command.
     *
     * @param description Description of the todo task.
     */
    public TodoCommand(String description) {
        this.description = description;
    }

    /**
     * Returns the command word represented by this command.
     *
     * @return The {@code todo} command word.
     */
    @Override
    public String getCommand() {
        return "todo";
    }

    /**
     * Validates and adds the todo task, then returns the response message.
     *
     * @param tasks Current task list.
     * @return Task-added response message.
     * @throws ZaruException If the todo description is empty or adding fails.
     */
    @Override
    public String execute(TaskList tasks) throws ZaruException {
        validateDescription(description, "The description of a todo cannot be empty.");
        return addTaskAndCreateResponse(tasks, new ToDo(description));
    }
}
