package nova.command;

import java.time.LocalDateTime;

import nova.exception.NovaException;
import nova.storage.Storage;
import nova.task.Deadline;
import nova.task.Event;
import nova.task.Task;
import nova.task.TaskDateTime;
import nova.task.TaskList;
import nova.task.Todo;
import nova.ui.Ui;

/**
 * Changes one detail of an existing task without changing its other properties.
 */
public class EditCommand extends TaskCommand {
    private final EditField field;
    private final String value;

    /**
     * Creates a command that edits one field of the selected task.
     *
     * @param taskNumber one-based number of the task to edit.
     * @param field task field to change.
     * @param value replacement value entered by the user.
     */
    public EditCommand(int taskNumber, EditField field, String value) {
        super(taskNumber, "edit");
        this.field = field;
        this.value = value;
    }

    /**
     * Replaces and saves a task, restoring the original task if saving fails.
     *
     * @param tasks task list to update.
     * @param ui user interface used to display the confirmation.
     * @param storage storage used to persist the updated list.
     * @throws NovaException if the edit is invalid or the updated list cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws NovaException {
        int taskIndex = resolveTaskIndex(tasks);
        Task originalTask = tasks.get(taskIndex);
        Task updatedTask = createUpdatedTask(originalTask);
        preserveCompletionStatus(originalTask, updatedTask);

        tasks.set(taskIndex, updatedTask);
        try {
            storage.save(tasks.getTasks());
        } catch (NovaException e) {
            tasks.set(taskIndex, originalTask);
            throw e;
        }
        ui.showTaskEdited(originalTask, updatedTask);
    }

    /**
     * Creates a replacement containing the requested edit.
     *
     * @param task original task.
     * @return replacement task with one changed field.
     * @throws NovaException if the selected field does not apply to the task.
     */
    private Task createUpdatedTask(Task task) throws NovaException {
        if (field == EditField.DESCRIPTION) {
            return createTaskWithDescription(task, value);
        }
        if (task instanceof Deadline deadline && field == EditField.BY) {
            TaskDateTime.ParsedValue due = TaskDateTime.parseForEdit(
                    value, field.getMarker(), deadline.getDueDateTime());
            return new Deadline(deadline.getDescription(), due.dateTime(), due.hasTime());
        }
        if (task instanceof Event event && (field == EditField.FROM || field == EditField.TO)) {
            return createEditedEvent(event);
        }

        throw new NovaException(getUnsupportedFieldMessage(task));
    }

    /**
     * Creates a task of the same type with a replacement description.
     *
     * @param task original task.
     * @param description replacement description.
     * @return task with the replacement description.
     */
    private Task createTaskWithDescription(Task task, String description) {
        if (task instanceof Todo) {
            return new Todo(description);
        }
        if (task instanceof Deadline deadline) {
            return new Deadline(description, deadline.getDueDateTime(), deadline.hasTime());
        }
        if (task instanceof Event event) {
            return new Event(description,
                    event.getStartDateTime(), event.hasStartTime(),
                    event.getEndDateTime(), event.hasEndTime());
        }

        assert false : "Every stored task must have a supported concrete type";
        throw new IllegalStateException("Unsupported task type: " + task.getClass().getName());
    }

    /**
     * Creates an event with either its start or end value changed.
     *
     * @param event original event.
     * @return event containing the edited endpoint.
     * @throws NovaException if the resulting range would be invalid.
     */
    private Task createEditedEvent(Event event) throws NovaException {
        LocalDateTime existingDateTime = field == EditField.FROM
                ? event.getStartDateTime()
                : event.getEndDateTime();
        TaskDateTime.ParsedValue replacement = TaskDateTime.parseForEdit(
                value, field.getMarker(), existingDateTime);

        LocalDateTime startDateTime = field == EditField.FROM
                ? replacement.dateTime()
                : event.getStartDateTime();
        boolean startHasTime = field == EditField.FROM
                ? replacement.hasTime()
                : event.hasStartTime();
        LocalDateTime endDateTime = field == EditField.TO
                ? replacement.dateTime()
                : event.getEndDateTime();
        boolean endHasTime = field == EditField.TO
                ? replacement.hasTime()
                : event.hasEndTime();

        if (endDateTime.isBefore(startDateTime)) {
            throw new NovaException("An event's /to date/time cannot be before its /from date/time.");
        }
        return new Event(event.getDescription(), startDateTime, startHasTime,
                endDateTime, endHasTime);
    }

    /**
     * Returns guidance for a field that does not belong to a task type.
     *
     * @param task task whose field selection is invalid.
     * @return user-facing validation message.
     */
    private String getUnsupportedFieldMessage(Task task) {
        if (task instanceof Todo) {
            return "A todo can only edit /description.";
        }
        if (task instanceof Deadline) {
            return "A deadline can only edit /description or /by.";
        }
        if (task instanceof Event) {
            return "An event can only edit /description, /from, or /to.";
        }

        assert false : "Every stored task must have a supported concrete type";
        return "This task type cannot be edited.";
    }

    /**
     * Copies the original task's completed state to its replacement.
     *
     * @param originalTask task before editing.
     * @param updatedTask replacement task.
     */
    private void preserveCompletionStatus(Task originalTask, Task updatedTask) {
        if (originalTask.isDone()) {
            updatedTask.markAsDone();
        }
    }
}
