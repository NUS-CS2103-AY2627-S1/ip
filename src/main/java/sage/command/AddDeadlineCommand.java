package sage.command;

import java.io.IOException;
import java.time.LocalDateTime;

import sage.exception.SageException;
import sage.model.Deadline;
import sage.model.TaskList;
import sage.parser.Parser;
import sage.storage.Storage;
import sage.ui.Ui;

/** Command that creates and saves a Deadline task. */
public class AddDeadlineCommand extends Command {
    private final String details;

    /**
     * Creates a Deadline command for raw deadline details.
     *
     * @param details the deadline description and due date or date-time
     */
    public AddDeadlineCommand(String details) {
        this.details = details;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws IOException, SageException {
        Parser parser = new Parser();
        String[] parts = parser.parseDeadlineDetails(details);
        assert parts.length == 2;
        LocalDateTime dateTime = parser.parseDateTimeOrDate(parts[1], false);
        Deadline deadline = new Deadline(parts[0], dateTime);
        if (tasks.containsEquivalent(deadline)) {
            throw new SageException("This task is already in your list.");
        }
        tasks.add(deadline);
        storage.save(tasks);
        showAdded(ui, tasks);
    }

    /** Displays confirmation and the updated task count after adding a deadline. */
    private void showAdded(Ui ui, TaskList tasks) {
        ui.show("Got it. I've added this task:");
        ui.show("  " + tasks.get(tasks.size() - 1));
        ui.show("Now you have " + tasks.size() + " task"
                + (tasks.size() == 1 ? "" : "s") + " in the list.");
    }
}
