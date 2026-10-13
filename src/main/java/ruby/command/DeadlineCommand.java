package ruby.command;

import ruby.RubyException;
import ruby.contact.ContactList;
import ruby.storage.Storage;
import ruby.task.Deadline;
import ruby.task.TaskList;

/**
 * Adds a deadline task to Ruby's task list.
 */
public class DeadlineCommand extends Command {
    private final Deadline deadline;

    /**
     * Creates a command that adds the parsed deadline.
     */
    public DeadlineCommand(Deadline deadline) {
        this.deadline = deadline;
    }

    @Override
    public String execute(TaskList tasks, ContactList contacts, Storage storage) throws RubyException {
        String response = tasks.addItem(deadline);
        storage.save(tasks, contacts);
        return response;
    }
}
