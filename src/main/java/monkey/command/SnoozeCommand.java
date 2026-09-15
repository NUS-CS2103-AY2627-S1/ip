package monkey.command;

import monkey.model.Deadline;
import monkey.model.Task;
import monkey.model.TaskList;
import monkey.storage.Storage;
import monkey.ui.Ui;

/** Command object for rescheduling an incomplete deadline. */
public class SnoozeCommand extends CommandAction {
    private static final String USAGE_MESSAGE = "Use: snooze <task number> <date or date-time>.";
    private static final String INVALID_NUMBER_MESSAGE =
            "OOPS! Monkey says: Please provide a valid task number after 'snooze'.";
    private static final String INVALID_DATE_MESSAGE =
            "A snoozed deadline must be in the future.";

    private final String arguments;

    /** Creates a command that reschedules a deadline using its raw arguments. */
    public SnoozeCommand(String arguments) {
        this.arguments = arguments == null ? "" : arguments.trim();
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        String[] parts = arguments.split("\\s+", 2);
        if (parts.length != 2 || parts[0].isEmpty() || parts[1].isEmpty()) {
            ui.showMessage(USAGE_MESSAGE);
            return;
        }

        int index;
        try {
            index = Integer.parseInt(parts[0]) - 1;
        } catch (NumberFormatException e) {
            ui.showMessage(INVALID_NUMBER_MESSAGE);
            return;
        }

        if (index < 0 || index >= tasks.size()) {
            ui.showMessage("That task number does not exist.");
            return;
        }

        Task task = tasks.get(index);
        if (!(task instanceof Deadline deadline)) {
            ui.showMessage("Only deadline tasks can be snoozed.");
            return;
        }
        if (deadline.isDone()) {
            ui.showMessage("Completed tasks cannot be snoozed.");
            return;
        }

        Deadline rescheduledDeadline;
        try {
            rescheduledDeadline = new Deadline(deadline.getDescription(), parts[1]);
        } catch (IllegalArgumentException e) {
            ui.showMessage(e.getMessage());
            return;
        }
        if (!rescheduledDeadline.isInFuture()) {
            ui.showMessage(INVALID_DATE_MESSAGE);
            return;
        }

        String previousDeadline = deadline.getStorageValue();
        deadline.reschedule(parts[1]);
        if (!saveTasks(tasks.asList(), ui, storage)) {
            deadline.reschedule(previousDeadline);
            return;
        }
        ui.showMessage("Snoozed this task to " + deadline.getBy() + ":");
        ui.showMessage("  " + deadline);
    }
}
