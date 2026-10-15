package wodan.command;

import wodan.WodanException;
import wodan.storage.Storage;
import wodan.task.Task;
import wodan.task.TaskList;
import wodan.ui.Ui;

/**
 * Adds a task (todo, deadline, or event) to the list and saves.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that will add {@code task}.
     *
     * @param task The task parsed from the user command.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds the stored task to {@code tasks}, saves the list, and shows the accepted-quest reply.
     *
     * @param tasks The task list to change.
     * @param ui The user interface for the reply.
     * @param storage The save file to update.
     * @throws WodanException If the quest is a duplicate, or the list cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws WodanException {
        if (tasks.containsSameDetails(task)) {
            throw new WodanException(
                    "That quest is already on the list. The ravens will not record it twice.");
        }
        tasks.add(task);
        storage.save(tasks.getTasks());
        ui.show("    You have accepted the following quest:",
                "        " + task.toString(),
                "     Now you have " + Ui.formatTaskCount(tasks.size()) + " in the list.");
    }
}
