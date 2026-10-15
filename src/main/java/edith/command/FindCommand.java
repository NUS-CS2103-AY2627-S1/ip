package edith.command;

import edith.task.TaskList;
import edith.ui.Ui;

/** Displays tasks matching a tag or containing given text in a description or tag. */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that searches tasks for the supplied text.
     *
     * @param keyword the word or phrase to search for
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui) {
        ui.showMatchingTasks(tasks.find(keyword));
    }
}
