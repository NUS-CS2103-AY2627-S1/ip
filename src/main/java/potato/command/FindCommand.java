package potato.command;

import potato.exception.PotatoException;
import potato.storage.Storage;
import potato.tasklist.TaskList;
import potato.ui.Ui;

/**
 * Command to search for tasks containing a specific keyword using Java Streams.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Constructs a {@code FindCommand} targeting the specified keyword.
     *
     * @param keyword Search keyword.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword.toLowerCase();
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws PotatoException {
        TaskList matchingTasks = tasks.findMatchingTasks(keyword);

        ui.showLine();
        if (matchingTasks.size() == 0) {
            ui.showMessage(" No matching tasks found in your list.");
        } else {
            StringBuilder matchingTasksMessage = new StringBuilder(" Here are the matching tasks in your list:\n");
            for (int i = 0; i < matchingTasks.size(); i++) {
                matchingTasksMessage.append(" ").append(i + 1).append(".").append(matchingTasks.get(i));
                if (i < matchingTasks.size() - 1) {
                    matchingTasksMessage.append("\n");
                }
            }
            ui.showMessage(matchingTasksMessage.toString());
        }
        ui.showLine();
    }
}
