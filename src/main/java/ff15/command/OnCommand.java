package ff15.command;

import java.util.List;

import ff15.Storage;
import ff15.Ui;
import ff15.contact.ContactList;
import ff15.task.DateRange;
import ff15.task.Task;
import ff15.task.TaskList;

/** Shows the tasks falling within a day, month, or year the user asked about. */
public class OnCommand extends Command {
    private final DateRange range;

    /**
     * Creates a command that will report the tasks falling in one span of dates.
     *
     * @param range the day, month, or year the user asked about.
     */
    public OnCommand(DateRange range) {
        this.range = range;
    }

    /** Shows the tasks falling within the range, or says there are none. */
    @Override
    public void execute(TaskList tasks, ContactList contacts, Ui ui, Storage storage) {
        List<Task> matches = tasks.tasksIn(range);
        if (matches.isEmpty()) {
            ui.showMessage("Nothing on " + range.getLabel()
                    + ". Conference room is free. I'm calling a meeting.");
        } else {
            ui.showTaskList("On " + range.getLabel() + " we've got:", matches);
        }
    }
}
