package bany.commands;

import bany.TaskFileRepository;
import bany.TaskStorage;
import bany.gui.Responder;

/** Returns every task currently stored by Bany. */
public class ListCommand extends Command {

    /** Creates a list command. */
    public ListCommand() {
    }

    /**
     * Returns every task currently held by the task store.
     *
     * @return command outcome containing the formatted task list.
     */
    @Override
    public CommandResult execute(TaskStorage tasks, Responder responder,
                        TaskFileRepository repository) {
        return CommandResult.success(
                ResponseMessage.info(
                        responder.respondList(tasks.getTasks())));
    }
}
