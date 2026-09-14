package arrodes.command;

import java.util.Locale;

import arrodes.storage.Storage;
import arrodes.task.Task;
import arrodes.task.TaskList;
import arrodes.ui.Ui;

/**
 * Searches the remembered tasks for descriptions containing a keyword.
 */
public class FindCommand extends Command {
    /** Keyword used for the case-insensitive description search. */
    private final String keyword;

    /**
     * Creates a command that searches task descriptions for the given keyword.
     *
     * @param keyword text to find in each task description
     */
    public FindCommand(String keyword) {
        assert keyword != null : "Find keyword must not be null.";
        this.keyword = keyword;
    }

    /**
     * Displays every task whose description contains the keyword.
     *
     * @param ui interface used to display the matching tasks
     * @param taskList list of tasks to search
     * @param storage storage used by commands that change tasks; unused here
     */
    @Override
    public void execute(Ui ui, TaskList taskList, Storage storage) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        boolean hasMatchingTask = false;
        ui.showMessage("Arrodes reveals the records matching your words:");
        for (int i = 0; i < taskList.getSize(); i++) {
            Task task = taskList.getTaskByIndex(i);
            if (task.getDescription().toLowerCase(Locale.ROOT).contains(normalizedKeyword)) {
                ui.showMessage((i + 1) + "." + task);
                hasMatchingTask = true;
            }
        }
        if (!hasMatchingTask) {
            ui.showMessage("The mirror finds no record matching those words.");
        }
    }
}
