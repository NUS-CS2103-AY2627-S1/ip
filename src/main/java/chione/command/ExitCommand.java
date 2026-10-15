package chione.command;

import chione.Storage;
import chione.Ui;
import chione.task.TaskList;

/**
 * Ends the conversation.
 */
public class ExitCommand extends Command {
    /** Creates the command; ending the conversation needs no further detail. */
    public ExitCommand() {
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        // Said here rather than by the run loop, because the window has no run
        // loop: it only ever sees what a command says. The text interface still
        // says goodbye of its own accord when the input runs out without a "bye"
        // -- when a script is piped in, for instance.
        ui.showGoodbye();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isExit() {
        return true;
    }
}
