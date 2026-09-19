package lizzy.command;

import lizzy.exception.LizzyException;
import lizzy.storage.Storage;
import lizzy.task.Event;
import lizzy.task.TaskList;
import lizzy.ui.Ui;

/**
 * Adds one parsed event to the task list.
 */
public class EventCommand extends Command {
    /**
     * The event to add.
     */
    private final Event event;

    /**
     * Creates a command that adds the supplied event.
     *
     * @param event the event to add
     */
    public EventCommand(Event event) {
        this.event = event;
    }

    /**
     * Adds, saves, and confirms the event.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws LizzyException {
        tasks.add(event);
        storage.save(tasks.asList());
        ui.showEventAdded(event, tasks.size());
    }
}
