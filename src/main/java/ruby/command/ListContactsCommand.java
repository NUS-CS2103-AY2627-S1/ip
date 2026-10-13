package ruby.command;

import ruby.contact.ContactList;
import ruby.storage.Storage;
import ruby.task.TaskList;

/**
 * Displays every contact in Ruby's contact list.
 */
public class ListContactsCommand extends Command {
    @Override
    public String execute(TaskList tasks, ContactList contacts, Storage storage) {
        return contacts.listContacts();
    }
}
