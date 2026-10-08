package rudra.command;

import java.util.ArrayList;

import rudra.exception.RudraException;
import rudra.storage.Storage;
import rudra.task.Event;
import rudra.task.Task;
import rudra.task.TaskDateTime;
import rudra.ui.Ui;

/**
 * Adds an event task to the task list.
 */
public class EventCommand extends Command {
    private final String description;
    private final TaskDateTime from;
    private final TaskDateTime to;

    /**
     * Creates a command that adds an event task.
     *
     * @param description Event description.
     * @param from Event start time.
     * @param to Event end time.
     */
    public EventCommand(String description, TaskDateTime from, TaskDateTime to) {
        this.description = description;
        this.from = from;
        this.to = to;
    }

    /**
     * Creates and saves the requested event task, then shows the confirmation message.
     *
     * @param tasks Current task list.
     * @param ui UI helper used to show output.
     * @param storage Storage helper used to persist changes.
     * @throws RudraException If the task cannot be saved.
     */
    @Override
    public void execute(ArrayList<Task> tasks, Ui ui, Storage storage) throws RudraException {
        Task eventTask = new Event(this.description, this.from, this.to);
        addTask(tasks, eventTask, storage);
        ui.showTaskAdded(eventTask, tasks.size());
    }
}
