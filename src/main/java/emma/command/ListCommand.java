package emma.command;

import emma.Storage;
import emma.TaskList;

/**
 * Shows every task Emma is tracking.
 */
public class ListCommand implements Command {

    @Override
    public String execute(TaskList tasks, Storage storage) {
        if (tasks.isEmpty()) {
            return "You haven't given me anything to track yet!";
        }
        return "Here's your tasks:\n" + tasks.format();
    }
}
