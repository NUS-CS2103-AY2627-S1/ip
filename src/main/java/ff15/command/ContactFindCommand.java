package ff15.command;

import java.util.List;

import ff15.Storage;
import ff15.Ui;
import ff15.contact.Contact;
import ff15.contact.ContactList;
import ff15.task.TaskList;

/** Shows the contacts whose name contains a supplied keyword. */
public class ContactFindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that will report the contacts matching one keyword.
     *
     * @param keyword the text to look for in each contact name.
     */
    public ContactFindCommand(String keyword) {
        this.keyword = keyword;
    }

    /** Shows the matching contacts, or says there are none. */
    @Override
    public void execute(TaskList tasks, ContactList contacts, Ui ui, Storage storage) {
        List<Contact> matches = contacts.find(keyword);
        if (matches.isEmpty()) {
            ui.showMessage("No '" + keyword + "' here. Is this someone from corporate?");
        } else {
            ui.showContactList("Found them. This is my office, I know everyone:", matches);
        }
    }
}
