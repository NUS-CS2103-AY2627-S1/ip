package chattingheads.command;

import chattingheads.task.TaskList;
import chattingheads.ui.Ui;

/**
 * Represents a command that lists all tasks containing a specified keyword.
 */
public class FindCommand extends Command {

    private final String keyword;

    /**
     * Creates a command to find tasks containing the specified keyword.
     *
     * @param keyword Keyword to search for.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String execute(TaskList taskList, Ui ui) {
        assert taskList != null : "TaskList should not be null";
        assert ui != null : "Ui should not be null";

        return ui.getFoundTasksMessage(taskList, keyword);
    }
}
