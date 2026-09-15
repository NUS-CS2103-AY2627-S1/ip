package duke.command;

import java.util.ArrayList;

import duke.task.Task;
import duke.task.TaskList;
import duke.ui.Ui;

/**
 * Finds tasks whose descriptions contain a given keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a find command.
     *
     * @param keyword Raw keyword string provided by the user.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Finds matching tasks and shows them via the UI.
     *
     * @param tasks Task list to search.
     * @param ui UI used to show results/errors.
     */
    @Override
    public void execute(TaskList tasks, Ui ui) {
        String cleanedKeyword = keyword.trim().replaceAll("\\s+", " ");
        if (cleanedKeyword.isEmpty()) {
            ui.showError("The find keyword cannot be empty!");
            return;
        }

        ArrayList<Task> matches = tasks.findByKeyword(cleanedKeyword);
        ui.showMatchingTasks(matches);
    }
}
