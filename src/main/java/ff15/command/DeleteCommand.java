package ff15.command;

import java.io.IOException;

import ff15.FF15Exception;
import ff15.Storage;
import ff15.Ui;
import ff15.contact.ContactList;
import ff15.task.Task;
import ff15.task.TaskList;

/** Removes a task from the list. */
public class DeleteCommand extends Command {
    private final int number;

    /**
     * Creates a command that will remove one task from the list.
     *
     * @param number the task number the user typed, counting from 1.
     */
    public DeleteCommand(int number) {
        this.number = number;
    }

    /**
     * Removes the numbered task, saves the list, and reports what was removed
     * along with how many tasks are left.
     *
     * @throws FF15Exception if the number does not name a task in the list.
     * @throws IOException if the updated list could not be saved.
     */
    @Override
    public void execute(TaskList tasks, ContactList contacts, Ui ui, Storage storage)
            throws FF15Exception, IOException {
        Task task = tasks.delete(number);
        storage.save(tasks);
        ui.showTaskRemoved(task, tasks.size());
    }
}
