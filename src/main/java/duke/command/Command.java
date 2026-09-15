package duke.command;

import duke.task.TaskList;
import duke.ui.Ui;

/**
 * Represents an executable user command.
 *
 * <p>Each command implementation encapsulates the logic required to handle a specific user instruction
 * (e.g., add a task, list tasks, exit the application).</p>
 */
public abstract class Command {

    /**
     * Executes the command.
     *
     * <p>Implementations may mutate the task list and/or display messages via the {@link Ui}.</p>
     *
     * @param tasks The task list to operate on.
     * @param ui The UI used to show messages/errors.
     */
    public abstract void execute(TaskList tasks, Ui ui);

    /**
     * Indicates whether executing this command should cause the application to exit.
     *
     * @return {@code true} if the application should terminate after this command, otherwise {@code false}.
     */
    public boolean isExit() {
        return false;
    }
}
