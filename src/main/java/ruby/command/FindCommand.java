package ruby.command;

import ruby.contact.ContactList;
import ruby.storage.Storage;
import ruby.task.TaskList;

/**
 * Finds tasks whose stored text contains the search keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that searches for tasks containing the given keyword.
     */
    public FindCommand(String keyword) {
        assert keyword != null : "Keyword cannot be null";
        this.keyword = keyword;
    }

    @Override
    public String execute(TaskList tasks, ContactList contacts, Storage storage) {
        assert tasks != null : "Task list cannot be null";
        return tasks.find(keyword);
    }
}
