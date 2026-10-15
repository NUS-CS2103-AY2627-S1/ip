package rex.command;

import rex.TaskList;
import rex.Ui;

/** Shows the tasks whose description contains a keyword. */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that searches for a keyword.
     *
     * @param keyword the text to look for in task descriptions.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Shows the matching tasks, each keeping the number it has in the full
     * list so that it can still be marked or deleted by that number.
     */
    @Override
    public void execute(TaskList tasks, Ui ui) {
        ui.showMatchingTasks(keyword, tasks, tasks.findIndicesMatching(keyword));
    }
}
