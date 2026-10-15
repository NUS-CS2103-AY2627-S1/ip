package stephen.command;

import stephen.exception.ChatbotException;
import stephen.storage.Storage;
import stephen.task.TaskList;
import stephen.ui.Ui;

/** Sorts the task list alphabetically by task description. */
public class SortCommand extends Command {
    /** Creates a command that sorts all tasks. */
    public SortCommand() {
    }

    /**
     * Sorts tasks, saves their new order, and displays the sorted list.
     *
     * @param tasks task list to sort
     * @param ui console UI used to display the sorted list
     * @param storage persistence service used to save the new order
     * @throws ChatbotException if the sorted task list cannot be saved
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ChatbotException {
        tasks.sortByDescription();
        saveTasks(tasks, storage);
        ui.showTasksSorted(tasks);
    }
}
