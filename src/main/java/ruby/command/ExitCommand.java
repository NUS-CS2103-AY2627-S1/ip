package ruby.command;

import ruby.contact.ContactList;
import ruby.storage.Storage;
import ruby.task.TaskList;

/**
 * Ends the Ruby application after displaying a farewell message.
 */
public class ExitCommand extends Command {
    @Override
    public String execute(TaskList tasks, ContactList contacts, Storage storage) {
        return "Bye. Your tasks are in precious hands.";
    }
}
