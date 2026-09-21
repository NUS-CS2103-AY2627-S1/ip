package echo.command;

import echo.storage.Storage;
import echo.task.TaskList;
import echo.ui.Ui;
//Codex assistance

/**
 * Shows every task currently in the list that matches the searchText.
 */
public class FindCommand extends Command {
    private final String searchText;

    /**
     * Creates a command that searches the list for task which description matches the searchText.
     * See: {@link Command#execute(TaskList, Ui, Storage)}
     * @param searchText Search string to match in the description of tasks.
     */
    public FindCommand(String searchText) {
        this.searchText = searchText;
    }
    /**
     * Prints the numbered listing of all tasks. Nothing is saved because
     * listing does not change any task.
     */
    @Override
    public String execute(TaskList taskList, Ui ui, Storage storage) {
        return ui.joinLines(taskList.searchListText(searchText));
    }
}
