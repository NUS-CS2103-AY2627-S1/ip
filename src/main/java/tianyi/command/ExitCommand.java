package tianyi.command;

import tianyi.Response;
import tianyi.storage.Storage;
import tianyi.task.TaskList;

/**
 * Ends the chatbot session.
 */
public class ExitCommand extends Command {
    private static final String FAREWELL = "Bye for now. Take care, and see you soon!";

    /**
     * Creates a command that ends the current application session.
     */
    public ExitCommand() {
    }

    /**
     * Returns the farewell message without changing stored tasks.
     *
     * @param tasks Task list, which is not modified.
     * @param storage Storage instance, which is not used.
     * @return Farewell message for the user.
     */
    @Override
    public Response execute(TaskList tasks, Storage storage) {
        return Response.exit(FAREWELL);
    }
}
