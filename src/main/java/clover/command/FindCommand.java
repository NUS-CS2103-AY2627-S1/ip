package clover.command;

import java.util.List;
import java.util.Locale;

import clover.exception.CloverException;
import clover.storage.Storage;
import clover.task.Task;
import clover.task.TaskList;
import clover.tutoree.TutoreeList;
import clover.ui.Ui;

/**
 * Finds a task by searching for a keyword in the task description from Clover's task list.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a FindCommand for the supplied search keyword.
     */
    public FindCommand(String argument) throws CloverException {
        String trimmed = argument.trim();
        if (trimmed.isEmpty()) {
            throw new CloverException("Give the grove a keyword to search for.");
        }
        this.keyword = trimmed.toLowerCase(Locale.ROOT);
    }

    /**
     * Displays tasks whose descriptions contain the keyword.
     */
    @Override
    public void execute(TaskList tasks, TutoreeList tutorees, Ui ui, Storage storage) throws CloverException {
        List<Task> matchingTasks = tasks.asList().stream()
                .filter(task -> task.getDescription().toLowerCase(Locale.ROOT).contains(keyword))
                .toList();
        ui.showFindResults(matchingTasks);
    }
}
