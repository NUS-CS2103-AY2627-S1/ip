package ruby.command;

import ruby.RubyException;
import ruby.contact.ContactList;
import ruby.storage.Storage;
import ruby.task.TaskList;

/**
 * Deletes one contact from Ruby's contact list.
 */
public class DeleteContactCommand extends Command {
    private final int index;

    /**
     * Creates a command for the given zero-based contact index.
     *
     * @param index Zero-based index of the contact to delete.
     */
    public DeleteContactCommand(int index) {
        this.index = index;
    }

    @Override
    public String execute(TaskList tasks, ContactList contacts, Storage storage) throws RubyException {
        String response = contacts.deleteContact(index);
        storage.save(tasks, contacts);
        return response;
    }
}
