package panda.command;

import panda.task.TaskList;
import panda.ui.Ui;

/**
 * Displays tasks whose descriptions contain a specified keyword.
 */
public class FindCommand implements Command {
    private final String keyword;

    /**
     * Creates a command to display tasks matching the specified keyword.
     *
     * @param keyword Keyword to find in task descriptions.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Retrieves and displays tasks whose descriptions contain the keyword.
     *
     * @param taskList Active task list.
     */
    @Override
    public void execute(TaskList taskList) {
        Ui.printMessage(taskList.getTasksWithKeyword(keyword));
    }
}
