package bany.commands;

import bany.TaskFileRepository;
import bany.TaskStorage;
import bany.gui.Responder;

/** Executes the command that ends the Bany session. */
public class ExitCommand extends Command {

    /** Creates an exit command. */
    public ExitCommand() {
    }

    /**
     * Returns the goodbye message and signals that the application should exit.
     *
     * @return exit command outcome.
     */
    @Override
    public CommandResult execute(TaskStorage tasks, Responder responder,
                        TaskFileRepository repository) {
        return CommandResult.exit(
                ResponseMessage.info(responder.respondGoodbye()));
    }
}
