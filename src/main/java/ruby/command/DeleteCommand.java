package ruby.command;

import ruby.RubyException;
import ruby.contact.ContactList;
import ruby.storage.Storage;
import ruby.task.TaskList;

/**
 * Removes one task from Ruby's task list.
 */
public class DeleteCommand extends Command {
    private final int index;

    /**
     * Creates a command for the given zero-based task index.
     */
    public DeleteCommand(int index) {
        this.index = index;
    }

    @Override
    public String execute(TaskList tasks, ContactList contacts, Storage storage) throws RubyException {
        String response = tasks.deleteItem(index);
        storage.save(tasks, contacts);
        return response;
    }
}
