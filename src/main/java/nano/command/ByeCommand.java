package nano.command;

import nano.Storage;
import nano.task.TaskList;
import nano.ui.Ui;

/**
 * Represents the bye command.
 */
public class ByeCommand implements Command {

    @Override
    public String execute(TaskList tasks, Storage storage) {
        return Ui.getGoodbyeMessage();
    }
}
