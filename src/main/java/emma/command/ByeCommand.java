package emma.command;

import emma.Storage;
import emma.TaskList;

/**
 * Ends the conversation.
 */
public class ByeCommand implements Command {

    private static final String GOODBYE = "Bye for now! Hope to see you again soon.";

    @Override
    public String execute(TaskList tasks, Storage storage) {
        return GOODBYE;
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
