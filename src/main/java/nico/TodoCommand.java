package nico;

import java.util.List;

/**
 * Adds a todo task to the task list.
 */
public class TodoCommand extends ParameterCommand {
    /**
     * Creates a command with the todo description supplied by the user.
     */
    public TodoCommand(String description) {
        super("todo", description);
    }

    /**
     * Validates the supplied description, adds a todo task, and saves it.
     *
     * @param tasks tasks currently managed by the chatbot.
     * @param ui user interface used to display confirmation.
     * @throws NicoException if the description is empty or the task cannot be saved
     */
    @Override
    public void execute(List<Task> tasks, Ui ui) throws NicoException {
        String description = getParameter();
        if (description == null || description.trim().isEmpty()) {
            throw NicoException.invalidInput("A to-do needs a description.", "todo DESCRIPTION");
        }
        Task newTodo = new Todo(description.trim());
        TaskStorage.writeTask(Nico.FILE_PATH, newTodo);
        tasks.add(newTodo);
        ui.showTaskAdded(newTodo, tasks.size());
    }
}
