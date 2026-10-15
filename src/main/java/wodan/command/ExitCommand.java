package wodan.command;

import wodan.storage.Storage;
import wodan.task.TaskList;
import wodan.ui.Ui;

/**
 * Stops the chatbot after showing the farewell message.
 */
public class ExitCommand extends Command {
    /**
     * Creates a command that stops the chatbot.
     */
    public ExitCommand() {
    }

    /**
     * Shows the farewell message.
     *
     * @param tasks Unused.
     * @param ui The user interface for the reply.
     * @param storage Unused.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /**
     * Returns {@code true} so the chatbot stops after this command.
     *
     * @return Always {@code true}.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
