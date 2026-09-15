package monkey.command;

import java.util.List;

import monkey.model.Deadline;
import monkey.model.Event;
import monkey.model.Task;
import monkey.model.TaskList;
import monkey.storage.Storage;
import monkey.ui.Ui;

/** Command object for adding deadline and event tasks. */
public class AddCommand extends CommandAction {
    private static final String FROM_MARKER = " /from ";
    private static final String TO_MARKER = " /to ";
    private static final String BY_MARKER = " /by ";

    private final Command type;
    private final String details;

    /** Creates a command for adding an event or deadline from its raw details. */
    public AddCommand(Command type, String details) {
        this.type = type;
        this.details = details;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        Task task;
        try {
            task = type == Command.EVENT ? createEvent(ui) : createDeadline(ui);
        } catch (IllegalArgumentException e) {
            ui.showMessage(e.getMessage());
            return;
        }
        if (task == null) {
            return;
        }

        if (tasks.containsEquivalent(task)) {
            ui.showMessage("OOPS! Monkey says: That task is already in your list.");
            return;
        }

        List<Task> proposedTasks = tasks.asList();
        proposedTasks.add(task);
        if (!saveTasks(proposedTasks, ui, storage)) {
            return;
        }
        tasks.add(task);
        ui.showMessage("Got it. I've added this task:");
        ui.showMessage("  " + task);
        ui.showMessage("Now you have " + tasks.size() + " tasks in the list.");
    }

    private Task createEvent(Ui ui) {
        int fromMarker = details.indexOf(FROM_MARKER);
        int toMarker = details.indexOf(TO_MARKER, fromMarker + 1);
        if (fromMarker < 0 || toMarker < 0 || fromMarker > toMarker
                || details.indexOf(FROM_MARKER, fromMarker + FROM_MARKER.length()) >= 0
                || details.indexOf(TO_MARKER, toMarker + TO_MARKER.length()) >= 0) {
            ui.showMessage("OOPS! Monkey says: Use one /from marker followed by one /to marker for an event.");
            return null;
        }
        String description = fromMarker >= 0 ? details.substring(0, fromMarker).trim() : details.trim();
        String from = fromMarker >= 0 && toMarker >= 0
                ? details.substring(fromMarker + FROM_MARKER.length(), toMarker).trim() : "";
        String to = toMarker >= 0 ? details.substring(toMarker + TO_MARKER.length()).trim() : "";

        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            ui.showMessage("OOPS! Monkey says: An event needs a description, a /from time, and a /to time. "
                    + "Even monkeys need a schedule!");
            return null;
        }
        return new Event(description, from, to);
    }

    private Task createDeadline(Ui ui) {
        int byMarker = details.indexOf(BY_MARKER);
        if (byMarker < 0 || details.indexOf(BY_MARKER, byMarker + BY_MARKER.length()) >= 0) {
            ui.showMessage("OOPS! Monkey says: Use exactly one /by marker for a deadline.");
            return null;
        }
        String description = byMarker >= 0 ? details.substring(0, byMarker).trim() : details.trim();
        String by = byMarker >= 0 ? details.substring(byMarker + BY_MARKER.length()).trim() : "";

        if (description.isEmpty() || by.isEmpty()) {
            ui.showMessage("OOPS! Monkey says: A deadline needs a description and a /by date or time. "
                    + "Don't let that banana go rotten!");
            return null;
        }
        return new Deadline(description, by);
    }
}
