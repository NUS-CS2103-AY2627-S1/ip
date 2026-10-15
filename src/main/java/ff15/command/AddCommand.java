package ff15.command;

import java.io.IOException;
import java.util.OptionalInt;

import ff15.FF15Exception;
import ff15.Storage;
import ff15.Ui;
import ff15.contact.ContactList;
import ff15.task.Task;
import ff15.task.TaskList;

/**
 * Adds a task to the list. Serves the todo, deadline, and event commands alike:
 * they differ in how the task was parsed, not in what happens to it afterwards.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that will add {@code task} to the list.
     *
     * @param task the task the parser built from the user input.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds the task, saves the updated list, and reports what was added. A task
     * the list already has is refused, naming the one it duplicates, since typing
     * the same thing twice is almost always a slip rather than a wish for two.
     *
     * @throws FF15Exception if the list already holds this task.
     * @throws IOException if the updated list could not be saved.
     */
    @Override
    public void execute(TaskList tasks, ContactList contacts, Ui ui, Storage storage)
            throws FF15Exception, IOException {
        OptionalInt existing = tasks.findSame(task);
        if (existing.isPresent()) {
            throw new FF15Exception("You already have that one. It's task " + existing.getAsInt()
                    + ". I remember everything.");
        }
        tasks.add(task);
        storage.save(tasks);
        ui.showTaskAdded(task, tasks.size());
    }
}
