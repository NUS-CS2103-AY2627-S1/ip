package lebron.command;

import java.util.List;

import lebron.task.Task;
import lebron.task.TaskList;
import lebron.ui.Ui;

/**
 * Represents the "find" command, which lists tasks whose description contains a keyword.
 */
public class FindCommand implements Command {
    private final String keyword;

    /**
     * Constructs a FindCommand for the given keyword.
     *
     * @param keyword The keyword to search for.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String execute(TaskList taskList, Ui ui) {
        List<Task> matches = taskList.findTasks(keyword);
        return ui.showMatchingTasks(matches);
    }
}
