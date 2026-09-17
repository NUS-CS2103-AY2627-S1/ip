package lebron.command;

import lebron.exception.LeBronException;
import lebron.task.TaskList;
import lebron.ui.Ui;

/**
 * Represents a single executable user command.
 */
public interface Command {
    /**
     * Executes this command against the given TaskList, reporting results via Ui.
     *
     * @param taskList The TaskList to act on.
     * @param ui The Ui to build the result message through.
     * @return The message describing the result of this command, for display to the user.
     *         Must never be null; {@link lebron.LeBron#getResponse(String)} asserts this.
     * @throws LeBronException if the command cannot be carried out.
     */
    String execute(TaskList taskList, Ui ui) throws LeBronException;

    /**
     * Returns whether this command should terminate the program after executing.
     *
     * @return true if the program should exit, false otherwise.
     */
    default boolean isExit() {
        return false;
    }
}
