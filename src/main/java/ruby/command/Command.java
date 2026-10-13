package ruby.command;

import ruby.RubyException;
import ruby.contact.ContactList;
import ruby.storage.Storage;
import ruby.task.TaskList;

/**
 * Represents one validated instruction the user has given Ruby.
 */
public abstract class Command {
    /**
     * Performs this command using the application's collaborators.
     *
     * @param tasks    Tasks managed by Ruby.
     * @param contacts Contacts managed by Ruby.
     * @param storage  Persists data when a command changes it.
     * @throws RubyException If the command cannot be completed.
     */
    public abstract String execute(TaskList tasks, ContactList contacts, Storage storage) throws RubyException;
}
