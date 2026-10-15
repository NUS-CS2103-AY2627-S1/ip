package piplupbot.command;

import piplupbot.PiplupBot;
import piplupbot.Storage;
import piplupbot.Ui;
import piplupbot.task.TaskList;

// ACKNOWLEDGEMENTS: This Java file was written with the help of Claude.

/**
 * Says goodbye and ends the conversation.
 *
 * <p>This is the only command that answers {@code true} to {@link #isExit()}.
 * Stopping is reported rather than done here: the command says the conversation
 * is over and {@link PiplupBot#run} is what actually stops reading, so a command
 * never has to know how the loop around it is written.</p>
 */
public class ExitCommand extends Command {
    /**
     * {@inheritDoc}
     *
     * <p>Saying goodbye is all this does. The list is unchanged, so there is
     * nothing to save, and stopping the conversation is left to
     * {@link #isExit()} rather than done here.</p>
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /**
     * {@inheritDoc}
     *
     * @return {@code true} always -- this is the one command that ends the
     *         conversation.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
