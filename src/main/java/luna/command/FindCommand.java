package luna.command;

import java.util.List;

import luna.LunaException;
import luna.storage.Storage;
import luna.task.TaskList;
import luna.ui.Ui;

/**
 * Represents the command that finds tasks whose descriptions contain a keyword.
 */
public class FindCommand extends Command {
    private final String input;

    /**
     * Creates a find command from the full user input.
     *
     * @param input Full command entered by the user.
     */
    public FindCommand(String input) {
        this.input = input;
    }

    /**
     * Finds tasks whose descriptions contain the requested keyword and shows the matches.
     *
     * @param tasks Current tasks in memory.
     * @param ui User interface for showing results.
     * @param storage Storage used to persist task changes.
     * @throws LunaException If the input does not contain a keyword.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws LunaException {
        String keyword = input.substring(4).trim();
        if (keyword.isEmpty()) {
            throw new LunaException("Please provide a keyword to search for.");
        }

        List<Integer> matchingTaskIndices = tasks.findTaskIndices(keyword);
        ui.showMatchingTasks(tasks, matchingTaskIndices);
    }
}
