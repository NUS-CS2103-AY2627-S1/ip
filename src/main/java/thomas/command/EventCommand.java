package thomas.command;

import thomas.ThomasException;
import thomas.storage.Storage;
import thomas.task.Event;
import thomas.task.Task;
import thomas.task.TaskList;
import thomas.ui.Ui;

/**
 * Adds an event task to the task list.
 */
public class EventCommand extends Command {
    private final String description;
    private final String from;
    private final String to;

    /**
     * Creates a command that adds an event task.
     *
     * @param description Description of the event.
     * @param from Start date/time of the event.
     * @param to End date/time of the event.
     */
    public EventCommand(String description, String from, String to) {
        this.description = description;
        this.from = from;
        this.to = to;
    }

    /**
     * Adds the event, saves the list, and confirms the addition.
     *
     * @param tasks task list to update
     * @param ui user interface used for output
     * @param storage storage used to persist the change
     * @throws ThomasException if a date/time is invalid or the end is before the start
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ThomasException {
        Task task = new Event(description, from, to);
        if (tasks.containsEquivalent(task)) {
            throw new ThomasException("This task already exists.");
        }
        tasks.add(task);
        storage.save(tasks);
        ui.showTaskAdded(task, tasks.size());
    }
}
