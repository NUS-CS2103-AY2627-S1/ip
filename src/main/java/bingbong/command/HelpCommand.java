package bingbong.command;

import bingbong.exception.BingBongException;
import bingbong.storage.Storage;
import bingbong.task.TaskList;
import bingbong.ui.Ui;

/**
 * Command to display an onboarding assistance reference panel summarizing all active chatbot capabilities.
 */
public class HelpCommand extends Command {

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BingBongException {
        StringBuilder sb = new StringBuilder();
        sb.append("[ BINGBONG COMMAND MANUAL ]\n\n");

        sb.append("[LIST] : Displays all active tasks.\n");
        sb.append("[TODO] (desc) : Sets a basic todo task.\n");
        sb.append("[DEADLINE] (desc) /by (time) : Sets a timed limit task.\n");
        sb.append("[EVENT] (desc) /from (start) /to (end) : Sets an event window.\n");
        sb.append("[MARK] (index) : Marks a task as completed.\n");
        sb.append("[UNMARK] (index) : Unmarks an incomplete task.\n");
        sb.append("[DELETE] (index) : Removes a task from the list.\n");
        sb.append("[FIND] (keyword) : Filters tasks matching text.\n");
        sb.append("[BYE] : Closes the chatbot application.");

        ui.print(sb.toString());
    }

    @Override
    public boolean isExit() {
        return false;
    }
}
