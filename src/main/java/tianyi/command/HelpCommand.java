package tianyi.command;

import tianyi.Response;
import tianyi.storage.Storage;
import tianyi.task.TaskList;

/**
 * Displays descriptions and examples for all supported commands.
 */
public class HelpCommand extends Command {
    /**
     * Creates a command that displays application help.
     */
    public HelpCommand() {
    }

    /**
     * Returns help text without changing stored tasks.
     *
     * @param tasks Task list, which is not modified.
     * @param storage Storage instance, which is not used.
     * @return Descriptions and examples for all supported commands.
     */
    @Override
    public Response execute(TaskList tasks, Storage storage) {
        return new Response(
                "Here is the list of commands:",
                CommandType.getCommands() + "\n",
                "I hope it helps you!"
        );
    }
}
