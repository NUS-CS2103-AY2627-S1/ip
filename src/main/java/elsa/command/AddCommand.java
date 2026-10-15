package elsa.command;

import elsa.ElsaException;
import elsa.storage.Storage;
import elsa.task.Task;
import elsa.task.TaskList;
import elsa.ui.Ui;

/**
 * Adds one task to the list.
 *
 * <p>One class covers todos, deadlines and events, because adding is the same
 * work whichever kind of task it is: the parser has already built the task, and
 * this only has to store it. What differs between the three is how they are
 * written, which is the parser's concern, not this one's.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that will add the given task.
     *
     * @param task the task the user described, already built by the parser
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws ElsaException {
        // The task is not written out again here. The only copy worth showing
        // would be the one already in the list, which may have been marked done
        // since; writing out the one just typed would claim a status it does not
        // have. The user can see what they typed directly above, either way.
        if (tasks.hasTask(task)) {
            throw new ElsaException("You already have that one, so I didn't add it twice."
                    + " Use \"list\" to see it.");
        }

        tasks.add(task);
        // Saved before the confirmation is worded, so the chatbot never claims to
        // have stored a task that did not reach the disk.
        storage.save(tasks);
        return ui.getAddedMessage(task, tasks.size());
    }
}
