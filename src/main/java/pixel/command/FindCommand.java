package pixel.command;

import pixel.storage.Storage;
import pixel.task.TaskList;
import pixel.ui.Ui;

/**
 * Displays tasks whose descriptions contain a given keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that searches for the specified keyword.
     *
     * @param keyword Keyword to search for.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMessage("Here are the matching tasks in your list:");

        int resultNumber = 1;

        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).containsKeyword(keyword)) {
                ui.showMessage(resultNumber + "." + tasks.get(i));
                resultNumber++;
            }
        }
    }
}
