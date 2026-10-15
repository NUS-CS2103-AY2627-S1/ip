package greenchonk.command;

import java.time.LocalDate;

import greenchonk.exception.GreenChonkException;
import greenchonk.storage.Storage;
import greenchonk.task.Deadline;
import greenchonk.task.Event;
import greenchonk.task.Task;
import greenchonk.task.TaskList;
import greenchonk.ui.Ui;

/**
 * Replaces one detail of an existing task while retaining its other details.
 */
public class EditCommand extends Command {
    private static final String COMMAND_NAME = "edit";

    private final EditField field;
    private final LocalDate newDate;
    private final String newDescription;
    private final int taskNumber;

    private enum EditField {
        DESCRIPTION,
        DUE_DATE,
        START_DATE,
        END_DATE
    }

    private EditCommand(int taskNumber, EditField field, String newDescription,
            LocalDate newDate) {
        assert field != null : "Edit field must not be null";
        assert (field == EditField.DESCRIPTION) == (newDescription != null)
                : "A description edit must contain only a description value";
        assert (field == EditField.DESCRIPTION) == (newDate == null)
                : "A date edit must contain only a date value";

        this.taskNumber = taskNumber;
        this.field = field;
        this.newDescription = newDescription;
        this.newDate = newDate;
    }

    /**
     * Creates a command that replaces a task's description.
     *
     * @param taskNumber the one-based task number to edit.
     * @param description the replacement description.
     * @return the configured edit command.
     */
    public static EditCommand editDescription(int taskNumber, String description) {
        return new EditCommand(taskNumber, EditField.DESCRIPTION, description, null);
    }

    /**
     * Creates a command that replaces a deadline's due date.
     *
     * @param taskNumber the one-based task number to edit.
     * @param dueDate the replacement due date.
     * @return the configured edit command.
     */
    public static EditCommand editDueDate(int taskNumber, LocalDate dueDate) {
        return new EditCommand(taskNumber, EditField.DUE_DATE, null, dueDate);
    }

    /**
     * Creates a command that replaces an event's starting date.
     *
     * @param taskNumber the one-based task number to edit.
     * @param startDate the replacement starting date.
     * @return the configured edit command.
     */
    public static EditCommand editStartDate(int taskNumber, LocalDate startDate) {
        return new EditCommand(taskNumber, EditField.START_DATE, null, startDate);
    }

    /**
     * Creates a command that replaces an event's ending date.
     *
     * @param taskNumber the one-based task number to edit.
     * @param endDate the replacement ending date.
     * @return the configured edit command.
     */
    public static EditCommand editEndDate(int taskNumber, LocalDate endDate) {
        return new EditCommand(taskNumber, EditField.END_DATE, null, endDate);
    }

    /**
     * Replaces one task detail and persists the updated task list.
     * Restores the original task if persistence fails.
     *
     * @param tasks the task list containing the task to edit.
     * @param ui the UI that reports the edited task.
     * @param storage the storage to which the updated list is saved.
     * @throws GreenChonkException if the edit is invalid or saving fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage)
            throws GreenChonkException {
        int taskIndex = getTaskIndex(taskNumber, COMMAND_NAME, tasks);
        Task originalTask = tasks.get(taskIndex);
        Task editedTask = createEditedTask(originalTask);

        if (!originalTask.hasSameDetails(editedTask) && tasks.containsEquivalent(editedTask)) {
            throw new GreenChonkException(
                    "Green Chonk is already carrying a task with those details.");
        }

        Task replacedTask = tasks.replace(taskIndex, editedTask);
        assert replacedTask == originalTask
                : "The edited task must replace the task selected by the user";
        try {
            storage.save(tasks);
        } catch (GreenChonkException exception) {
            tasks.replace(taskIndex, originalTask);
            throw exception;
        }
        ui.showTaskEdited(originalTask, editedTask);
    }

    /**
     * Creates the edited task while enforcing type-specific field rules.
     *
     * @param task the original task.
     * @return the edited task.
     * @throws GreenChonkException if the requested field does not apply to the task.
     */
    private Task createEditedTask(Task task) throws GreenChonkException {
        switch (field) {
            case DESCRIPTION:
                return task.withDescription(newDescription);
            case DUE_DATE:
                if (!(task instanceof Deadline deadline)) {
                    throw new GreenChonkException("Task " + taskNumber
                            + " is not a deadline. Use /by only with a deadline.");
                }
                return deadline.withDueDate(newDate);
            case START_DATE:
                if (!(task instanceof Event event)) {
                    throw new GreenChonkException("Task " + taskNumber
                            + " is not an event. Use /from and /to only with an event.");
                }
                return applyStartDateEdit(event);
            case END_DATE:
                if (!(task instanceof Event event)) {
                    throw new GreenChonkException("Task " + taskNumber
                            + " is not an event. Use /from and /to only with an event.");
                }
                return applyEndDateEdit(event);
            default:
                throw new AssertionError("Unhandled edit field: " + field);
        }
    }

    /**
     * Replaces an event's starting date and translates an invalid range for the user.
     *
     * @param event the event to edit.
     * @return the edited event.
     * @throws GreenChonkException if the starting date is after the ending date.
     */
    private Event applyStartDateEdit(Event event) throws GreenChonkException {
        try {
            return event.withStartDate(newDate);
        } catch (IllegalArgumentException exception) {
            throw new GreenChonkException("An event's start date cannot be after its end date. "
                    + "Try /from " + event.getEndDate() + " or earlier.");
        }
    }

    /**
     * Replaces an event's ending date and translates an invalid range for the user.
     *
     * @param event the event to edit.
     * @return the edited event.
     * @throws GreenChonkException if the ending date is before the starting date.
     */
    private Event applyEndDateEdit(Event event) throws GreenChonkException {
        try {
            return event.withEndDate(newDate);
        } catch (IllegalArgumentException exception) {
            throw new GreenChonkException("An event's end date cannot be before its start date. "
                    + "Try /to " + event.getStartDate() + " or later.");
        }
    }
}
