package yqr.command;

import yqr.storage.Storage;
import yqr.task.TaskList;
import yqr.ui.Ui;

/**
 * Ends the chatbot session.
 */
public class ExitCommand extends Command {
    /**
     * Creates a command that ends the chatbot session.
     */
    public ExitCommand() {
    }

    /**
     * Displays the farewell message.
     *
     * @param tasks task list, which this command does not modify.
     * @param ui user interface used to display the farewell.
     * @param storage storage component, which this command does not modify.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /**
     * Indicates that the application should exit after this command.
     *
     * @return {@code true}.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
