package echo.command;

import echo.storage.Storage;
import echo.task.TaskList;
import echo.ui.Ui;

/**
 * Ends the chatbot session.
 */
public class ExitCommand extends Command {
    /**
     * Sends the farewell message.
     * See: {@link Command#execute(TaskList, Ui, Storage)}
     */
    @Override
    public String execute(TaskList taskList, Ui ui, Storage storage) {
        return ui.farewell();
    }

    /**
     * Indicates that the command loop should end.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
