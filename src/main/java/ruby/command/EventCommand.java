package ruby.command;

import ruby.RubyException;
import ruby.contact.ContactList;
import ruby.storage.Storage;
import ruby.task.Event;
import ruby.task.TaskList;

/**
 * Adds an event task to Ruby's task list.
 */
public class EventCommand extends Command {
    private final Event event;

    /**
     * Creates a command that adds the parsed event.
     */
    public EventCommand(Event event) {
        this.event = event;
    }

    @Override
    public String execute(TaskList tasks, ContactList contacts, Storage storage) throws RubyException {
        String response = tasks.addItem(event);
        storage.save(tasks, contacts);
        return response;
    }
}
