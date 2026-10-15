package kiwi.command;

import kiwi.Storage;
import kiwi.Ui;
import kiwi.task.TaskList;

/**
 * Ends the chatbot session and shows the goodbye message.
 */
public class ExitCommand extends Command {

    /**
     * Shows the goodbye message. Does not change the task list or save file.
     *
     * {@inheritDoc}
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /**
     * {@inheritDoc}
     *
     * @return {@code true} so the chatbot loop can end.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
