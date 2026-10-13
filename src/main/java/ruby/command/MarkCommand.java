package ruby.command;

import ruby.RubyException;
import ruby.contact.ContactList;
import ruby.storage.Storage;
import ruby.task.TaskList;

/**
 * Marks one task as complete.
 */
public class MarkCommand extends Command {
    private final int index;

    /**
     * Creates a command for the given zero-based task index.
     */
    public MarkCommand(int index) {
        this.index = index;
    }

    @Override
    public String execute(TaskList tasks, ContactList contacts, Storage storage) throws RubyException {
        String response = tasks.markItem(index);
        storage.save(tasks, contacts);
        return response;
    }
}
