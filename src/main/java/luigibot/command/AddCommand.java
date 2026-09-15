package luigibot.command;

import java.util.List;

import luigibot.storage.Storage;
import luigibot.task.Event;
import luigibot.task.Task;
import luigibot.task.TaskList;
import luigibot.ui.Ui;

/**
 * Adds a task to LuigiBot's task list.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that adds the given task.
     *
     * @param task task to add.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds the task, saves the updated list, and displays a confirmation.
     *
     * @param tasks stored tasks.
     * @param ui user interface used to display the confirmation.
     * @param storage storage used to persist the updated task list.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        if (this.task instanceof Event event) {
            List<Integer> clashingIndexes = tasks.findIndexesClashingWith(event);
            if (!clashingIndexes.isEmpty()) {
                ui.showEventClashes(tasks, clashingIndexes);
                return;
            }
        }

        tasks.add(this.task);
        storage.save(tasks, ui);
        ui.showTaskAdded(this.task, tasks.size());
    }
}
