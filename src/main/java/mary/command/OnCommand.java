package mary.command;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import mary.exception.MaryException;
import mary.storage.Storage;
import mary.task.Deadline;
import mary.task.Event;
import mary.task.Task;
import mary.task.TaskList;
import mary.ui.Ui;

/**
 * Displays deadlines and events occurring on a date.
 */
public class OnCommand extends Command {
    private final String fullCommand;

    /**
     * Creates a command retaining the supplied input for execution-time validation.
     *
     * @param fullCommand complete, non-null user input.
     */
    public OnCommand(String fullCommand) {
        this.fullCommand = fullCommand;
    }

    /**
     * Displays matching deadlines and events for the requested date, including both event boundary dates.
     * Todos are excluded and the list is not modified; invalid dates are reported through the UI.
     *
     * @param tasks current in-memory task list.
     * @param ui user-facing message handler.
     * @param storage persistence service (unused by read-only commands).
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        try {
            if (!fullCommand.startsWith("on ") || fullCommand.substring(3).trim().isEmpty()) {
                throw new MaryException("use 'on d/M/yyyy', for example 'on 2/12/2019'.");
            }
            LocalDate date;
            try {
                date = Task.parseDate(fullCommand.substring(3).trim());
            } catch (DateTimeParseException exception) {
                throw new MaryException("use date format d/M/yyyy, for example 2/12/2019.");
            }
            boolean isFound = false;
            for (Task task : tasks.getTasks()) {
                boolean doesOccur = task instanceof Deadline && ((Deadline) task).getBy().toLocalDate().equals(date)
                        || task instanceof Event && !((Event) task).getFrom().toLocalDate().isAfter(date)
                        && !((Event) task).getTo().toLocalDate().isBefore(date);
                if (doesOccur) {
                    if (!isFound) {
                        ui.showMessage(" Tasks occurring on " + date + ":");
                    }
                    isFound = true;
                    ui.showMessage(" " + task);
                }
            }
            if (!isFound) {
                ui.showMessage(" No deadlines or events occur on " + date + ".");
            }
        } catch (MaryException exception) {
            ui.showError(exception.getMessage());
        }
    }
}
