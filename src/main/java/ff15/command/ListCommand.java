package ff15.command;

import ff15.Storage;
import ff15.Ui;
import ff15.contact.ContactList;
import ff15.task.TaskList;

/** Shows every task in the list, in order. */
public class ListCommand extends Command {
    /** Prints every task in the list, numbered from 1, or a remark on the emptiness. */
    @Override
    public void execute(TaskList tasks, ContactList contacts, Ui ui, Storage storage) {
        if (tasks.size() == 0) {
            ui.showMessage("Nothing on the list. Just like Toby's contribution to this office.");
        } else {
            ui.showTaskList("Here's what we're working with, people:", tasks.asList());
        }
    }
}
