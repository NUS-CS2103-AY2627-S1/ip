package ruby.command;

import ruby.contact.ContactList;
import ruby.storage.Storage;
import ruby.task.TaskList;

/**
 * Displays every task in Ruby's task list.
 */
public class ListCommand extends Command {
    @Override
    public String execute(TaskList tasks, ContactList contacts, Storage storage) {
        return tasks.listItems();
    }
}
