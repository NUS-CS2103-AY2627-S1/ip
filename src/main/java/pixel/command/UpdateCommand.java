package pixel.command;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import pixel.storage.Storage;
import pixel.task.Deadline;
import pixel.task.Event;
import pixel.task.Task;
import pixel.task.TaskList;
import pixel.ui.Ui;

/**
 * Updates one detail of an existing task.
 */
public class UpdateCommand extends Command {
    private final int index;
    private final UpdateField field;
    private final String value;

    /**
     * Creates a command that updates one task detail.
     *
     * @param index Zero-based task index.
     * @param field Detail to update.
     * @param value Replacement value.
     */
    public UpdateCommand(int index, UpdateField field, String value) {
        this.index = index;
        this.field = field;
        this.value = value;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        if (!tasks.isValidIndex(index)) {
            ui.showMessage("That task number does not exist.");
            return;
        }

        Task updatedTask = createUpdatedTask(tasks.get(index));
        if (tasks.containsSameDetailsExcept(updatedTask, index)) {
            throw new IllegalArgumentException(
                    "That update would duplicate another task in your list.");
        }
        tasks.replace(index, updatedTask);
        saveTasksSafely(tasks, ui, storage);
        ui.showMessage("Got it. I've updated this task:");
        ui.showMessage("  " + updatedTask);
    }

    private Task createUpdatedTask(Task task) {
        return switch (field) {
            case DESCRIPTION -> task.withDescription(value);
            case BY -> updateDeadline(task);
            case FROM -> updateEventStart(task);
            case TO -> updateEventEnd(task);
        };
    }

    private Task updateDeadline(Task task) {
        if (!(task instanceof Deadline deadline)) {
            throw new IllegalArgumentException("Only deadlines have a /by date.");
        }
        try {
            return deadline.withDeadline(LocalDate.parse(value));
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException(
                    "Oops! Please enter the deadline date in YYYY-MM-DD format.", exception);
        }
    }

    private Task updateEventStart(Task task) {
        if (!(task instanceof Event event)) {
            throw new IllegalArgumentException("Only events have a /from time.");
        }
        return event.withStart(value);
    }

    private Task updateEventEnd(Task task) {
        if (!(task instanceof Event event)) {
            throw new IllegalArgumentException("Only events have a /to time.");
        }
        return event.withEnd(value);
    }
}
