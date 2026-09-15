package duke.command;

import duke.task.TaskList;
import duke.ui.Ui;

/**
 * Adds a todo task.
 */
public class TodoCommand extends Command {
    /** Raw description string provided by the user. */
    private final String description;

    /**
     * Creates a todo command with the given description.
     *
     * @param description Raw description string (may include surrounding whitespace).
     */
    public TodoCommand(String description) {
        this.description = description;
    }

    /**
     * Adds a todo task to the task list if the description is non-empty.
     *
     * @param tasks Task list to add into.
     * @param ui UI used to show success/error messages.
     */
    @Override
    public void execute(TaskList tasks, Ui ui) {
        String cleanedDescription = description.trim();
        if (cleanedDescription.isEmpty()) {
            ui.showError("The Todo description cannot be empty!");
            return;
        }

        tasks.addTodo(cleanedDescription);
        ui.showTaskAdded(tasks.getLast(), tasks.size());
    }
}
