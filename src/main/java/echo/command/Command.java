package echo.command;

import echo.exception.EchoException;
import echo.storage.Storage;
import echo.task.TaskList;
import echo.ui.Ui;

/**
 * A user command ready to be executed against the chatbot's components.
 * Each concrete subclass knows both what to do with the task list and how
 * to report the outcome through the UI, so Echo only has to call
 * {@link #execute}.
 */
public abstract class Command {
    /**
     * Performs this command's action, reporting feedback via the given UI
     * and persisting any changes via storage.
     *
     * @param taskList task list to operate on
     * @param ui output channel for user-facing messages
     * @param storage persistence layer for saving mutations
     * @return response of Echo
     * @throws EchoException when the action fails, e.g. an unknown task number
     */
    public abstract String execute(TaskList taskList, Ui ui, Storage storage) throws EchoException;

    /**
     * Indicates whether executing this command should end the program.
     *
     * @return true only if this command exits the program
     */
    public boolean isExit() {
        return false;
    }
}
