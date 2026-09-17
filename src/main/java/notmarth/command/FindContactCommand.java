package notmarth.command;

import java.util.Locale;

import notmarth.model.Contact;
import notmarth.model.ContactList;
import notmarth.model.TaskList;
import notmarth.storage.Storage;
import notmarth.ui.Ui;

/** Searches contact names case-insensitively. */
public final class FindContactCommand extends Command {
    private final String keyword;

    /** Creates a contact-search command. */
    public FindContactCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, ContactList contacts, Ui ui, Storage storage) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        boolean hasMatch = false;
        for (int i = 0; i < contacts.size(); i++) {
            Contact contact = contacts.get(i);
            if (contact.getName().toLowerCase(Locale.ROOT).contains(normalizedKeyword)) {
                if (!hasMatch) {
                    ui.showFindContactsHeader();
                }
                ui.showNumberedContact(i + 1, contact);
                hasMatch = true;
            }
        }
        if (!hasMatch) {
            ui.showNoFindContacts(keyword);
        }
    }
}
