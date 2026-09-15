package luigibot.command;

import java.util.List;

import luigibot.storage.Storage;
import luigibot.task.TaskList;
import luigibot.ui.Ui;

/**
 * Displays tasks whose descriptions contain a keyword.
 */
public class FindKeywordCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that searches task descriptions for a keyword.
     *
     * @param keyword keyword to search for.
     */
    public FindKeywordCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Finds and displays tasks containing the keyword.
     *
     * @param tasks stored tasks.
     * @param ui user interface used to display matching tasks.
     * @param storage storage dependency required by the common command interface.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        List<Integer> matchingIndexes = tasks.findIndexesByKeyword(this.keyword);
        ui.showTasksMatchingKeyword(this.keyword, tasks, matchingIndexes);
    }
}
