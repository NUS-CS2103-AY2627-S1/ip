package jiji.command;

import jiji.exception.JijiException;
import jiji.storage.Storage;
import jiji.task.Event;
import jiji.task.Task;
import jiji.task.TaskList;
import jiji.ui.Ui;

/**
 * Command that adds a new {@link Event} task to the task list.
 */
public class AddEventCommand extends Command {

    private final String description;
    private final String from;
    private final String to;

    /**
     * Constructs an AddEventCommand with the specified description, start time, and end time.
     *
     * @param description Description of the event task.
     * @param from Start date/time string.
     * @param to End date/time string.
     */
    public AddEventCommand(String description, String from, String to) {
        this.description = description;
        this.from = from;
        this.to = to;
    }

    /**
     * Executes the add event command, persisting changes to storage and updating the UI.
     *
     * @param tasks The task list.
     * @param ui The UI handler for displaying output.
     * @param storage The storage handler for saving tasks.
     * @throws JijiException If saving to storage fails, dates are inverted, or duplicate task exists.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws JijiException {
        assert tasks != null : "TaskList dependency cannot be null";
        assert ui != null : "Ui dependency cannot be null";
        assert storage != null : "Storage dependency cannot be null";
        Event event = new Event(description, from, to);
        if (event.isEndBeforeStart()) {
            throw new JijiException("OOPS! ^๑_๑^ ੭ Event end date/time cannot be earlier than start date/time.");
        }
        Task duplicate = tasks.findDuplicate(event);
        if (duplicate != null) {
            throw new JijiException("OOPS! ₍^. .^₎ This task is already in your list:\n  " + duplicate);
        }
        tasks.add(event);
        storage.save(tasks);
        ui.showTaskAdded(event, tasks.size());
        return ui.formatTaskAdded(event, tasks.size());
    }
}
