package ff15.command;

import java.io.IOException;

import ff15.FF15Exception;
import ff15.Storage;
import ff15.Ui;
import ff15.contact.ContactList;
import ff15.task.Task;
import ff15.task.TaskList;

/** Marks a task as not done again. */
public class UnmarkCommand extends Command {
    private final int number;

    /**
     * Creates a command that will mark one task as not done again.
     *
     * @param number the task number the user typed, counting from 1.
     */
    public UnmarkCommand(int number) {
        this.number = number;
    }

    /**
     * Marks the numbered task as not done, saves the list, and shows the updated task.
     *
     * @throws FF15Exception if the number does not name a task in the list.
     * @throws IOException if the updated list could not be saved.
     */
    @Override
    public void execute(TaskList tasks, ContactList contacts, Ui ui, Storage storage)
            throws FF15Exception, IOException {
        Task task = tasks.get(number);
        task.markAsNotDone();
        storage.save(tasks);
        ui.showTask("Un-done. Like me and Jan. Anyway:", task);
    }
}
