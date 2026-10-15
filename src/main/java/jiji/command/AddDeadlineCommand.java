package jiji.command;

import jiji.exception.JijiException;
import jiji.storage.Storage;
import jiji.task.Deadline;
import jiji.task.Task;
import jiji.task.TaskList;
import jiji.ui.Ui;

/**
 * Command that adds a new {@link Deadline} task to the task list.
 */
public class AddDeadlineCommand extends Command {

    private final String description;
    private final String by;

    /**
     * Constructs an AddDeadlineCommand with the specified description and deadline string.
     *
     * @param description Description of the deadline task.
     * @param by Deadline date or time string.
     */
    public AddDeadlineCommand(String description, String by) {
        this.description = description;
        this.by = by;
    }

    /**
     * Executes the add deadline command, persisting changes to storage and updating the UI.
     *
     * @param tasks The task list.
     * @param ui The UI handler for displaying output.
     * @param storage The storage handler for saving tasks.
     * @throws JijiException If saving to storage fails or duplicate task exists.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws JijiException {
        assert tasks != null : "TaskList dependency cannot be null";
        assert ui != null : "Ui dependency cannot be null";
        assert storage != null : "Storage dependency cannot be null";
        Task deadline = new Deadline(description, by);
        Task duplicate = tasks.findDuplicate(deadline);
        if (duplicate != null) {
            throw new JijiException("OOPS! ₍^. .^₎ This task is already in your list:\n  " + duplicate);
        }
        tasks.add(deadline);
        storage.save(tasks);
        ui.showTaskAdded(deadline, tasks.size());
        return ui.formatTaskAdded(deadline, tasks.size());
    }
}
