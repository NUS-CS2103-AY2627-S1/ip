package dukey.command;

import dukey.exception.DukeyException;
import dukey.storage.Storage;
import dukey.task.Event;
import dukey.task.Task;
import dukey.task.TaskList;
import dukey.ui.Ui;

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
     * @param description description of the event task.
     * @param from start date/time of the event.
     * @param to end date/time of the event.
     */
    public EventCommand(String description, String from, String to) {
        this.description = description;
        this.from = from;
        this.to = to;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DukeyException {
        Task task = new Event(this.description, this.from, this.to);
        tasks.add(task);
        storage.save(tasks);
        ui.showTaskAdded(task, tasks);
    }

    @Override
    public String getResponseStyleClass() {
        return STYLE_ADD;
    }
}
