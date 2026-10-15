package gunna.command;

import gunna.Storage;
import gunna.TaskList;
import gunna.Ui;
/**
 * Represents a command to list all tasks.
 */
public class ListCommand extends Command {

    /**
     * Executes the list command by displaying all tasks to the user.
     *
     * @param tasks The task list to display.
     * @param ui The UI to show the task list.
     * @param storage The storage (not used).
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks.getTasks());
        StringBuilder response = new StringBuilder("Current queue:");
        for (int i = 0; i < tasks.size(); i++) {
            response.append("\n").append(i + 1).append(".").append(tasks.get(i));
        }
        return response.toString();
    }
}
