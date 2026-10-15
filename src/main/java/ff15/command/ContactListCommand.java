package ff15.command;

import ff15.Storage;
import ff15.Ui;
import ff15.contact.ContactList;
import ff15.task.TaskList;

/** Shows every contact in the list, in order. */
public class ContactListCommand extends Command {
    /** Prints every contact in the list, numbered from 1. */
    @Override
    public void execute(TaskList tasks, ContactList contacts, Ui ui, Storage storage) {
        ui.showContactList("Here are the contacts in your list:", contacts.asList());
    }
}
