package ff15.command;

import java.io.IOException;

import ff15.FF15Exception;
import ff15.Storage;
import ff15.Ui;
import ff15.contact.ContactList;
import ff15.task.Task;
import ff15.task.TaskList;

/** Marks a task as done. */
public class MarkCommand extends Command {
    private final int number;

    /**
     * Creates a command that will mark one task as done.
     *
     * @param number the task number the user typed, counting from 1.
     */
    public MarkCommand(int number) {
        this.number = number;
    }

    /**
     * Marks the numbered task as done, saves the list, and shows the updated task.
     *
     * @throws FF15Exception if the number does not name a task in the list.
     * @throws IOException if the updated list could not be saved.
     */
    @Override
    public void execute(TaskList tasks, ContactList contacts, Ui ui, Storage storage)
            throws FF15Exception, IOException {
        Task task = tasks.get(number);
        task.markAsDone();
        storage.save(tasks);
        ui.showTask("Boom. Done. That's a Dundie right there:", task);
    }
}
