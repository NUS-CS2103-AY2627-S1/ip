package ruby.command;

import ruby.RubyException;
import ruby.contact.ContactList;
import ruby.storage.Storage;
import ruby.task.TaskList;

/**
 * Marks one task as incomplete.
 */
public class UnmarkCommand extends Command {
    private final int index;

    /**
     * Creates a command for the given zero-based task index.
     */
    public UnmarkCommand(int index) {
        this.index = index;
    }

    @Override
    public String execute(TaskList tasks, ContactList contacts, Storage storage) throws RubyException {
        String response = tasks.unmarkItem(index);
        storage.save(tasks, contacts);
        return response;
    }
}
