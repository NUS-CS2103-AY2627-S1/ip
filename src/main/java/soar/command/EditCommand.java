package soar.command;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import soar.exception.InvalidTaskFormatException;
import soar.exception.SoarException;
import soar.parser.CommandType;
import soar.parser.DateTimeParser;
import soar.storage.Storage;
import soar.task.Deadline;
import soar.task.Event;
import soar.task.Task;
import soar.task.TaskList;
import soar.task.ToDo;
import soar.ui.Ui;

/**
 * Replaces selected details of one task and persists the updated task list.
 */
public class EditCommand extends Command {
    /** One-based task number entered by the user. */
    private final int taskNumber;

    /** New field values in the order entered by the user. */
    private final LinkedHashMap<EditField, String> edits;

    /**
     * Creates an edit command for validated field syntax.
     *
     * @param taskNumber One-based task number entered by the user.
     * @param edits Non-empty field values in command order.
     */
    public EditCommand(int taskNumber, Map<EditField, String> edits) {
        this.taskNumber = taskNumber;
        Objects.requireNonNull(edits, "Edits must not be null");
        if (edits.isEmpty()) {
            throw new IllegalArgumentException("At least one edit must be supplied");
        }
        this.edits = new LinkedHashMap<>(edits);
    }

    /**
     * Validates and applies all requested fields as one persisted change.
     *
     * @param tasks Task list to update.
     * @param ui Interface used to show the result.
     * @param storage Storage used to persist the updated list.
     * @throws SoarException if the target, fields, values, or save are invalid
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws SoarException {
        int taskIndex = requireTaskIndex(taskNumber, tasks, CommandType.EDIT);
        Task originalTask = tasks.get(taskIndex);
        Task updatedTask = createUpdatedTask(originalTask);
        copyCompletionState(originalTask, updatedTask);

        if (originalTask.toDataString().equals(updatedTask.toDataString())) {
            ui.showTaskUnchanged(originalTask);
            return;
        }

        Task replacedTask = tasks.replace(taskIndex, updatedTask);
        assert replacedTask == originalTask : "Editing should replace the selected task";
        saveChange(storage, tasks, () -> {
            Task rolledBackTask = tasks.replace(taskIndex, originalTask);
            assert rolledBackTask == updatedTask : "Edit rollback should remove the updated task";
        });
        ui.showTaskEdited(originalTask, updatedTask);
    }

    /** Builds a same-type replacement after validating fields for the selected task. */
    private Task createUpdatedTask(Task task) throws InvalidTaskFormatException {
        if (task instanceof ToDo) {
            requireSupportedFields(Set.of(EditField.DESCRIPTION),
                    "A todo can only edit /description.");
            return new ToDo(editedDescription(task));
        }
        if (task instanceof Deadline deadline) {
            requireSupportedFields(Set.of(EditField.DESCRIPTION, EditField.BY),
                    "A deadline can only edit /description or /by.");
            return createUpdatedDeadline(deadline);
        }
        if (task instanceof Event event) {
            requireSupportedFields(Set.of(EditField.DESCRIPTION, EditField.FROM, EditField.TO),
                    "An event can only edit /description, /from, or /to.");
            return createUpdatedEvent(event);
        }
        throw new IllegalArgumentException("Unsupported task type: " + task.getClass().getName());
    }

    /** Builds a deadline using either its existing or newly parsed due value. */
    private Deadline createUpdatedDeadline(Deadline deadline) throws InvalidTaskFormatException {
        String description = editedDescription(deadline);
        String editedBy = edits.get(EditField.BY);
        if (editedBy == null) {
            return deadline.hasTime()
                    ? new Deadline(description, deadline.getBy())
                    : new Deadline(description, deadline.getBy().toLocalDate());
        }

        LocalDateTime dateTime = DateTimeParser.parseDateTime(editedBy).orElse(null);
        if (dateTime != null) {
            return new Deadline(description, dateTime);
        }
        LocalDate date = DateTimeParser.parseDate(editedBy).orElse(null);
        if (date != null) {
            return new Deadline(description, date);
        }
        throw new InvalidTaskFormatException("I couldn't understand the deadline '" + editedBy
                + "'. Use yyyy-MM-dd, d/M/yyyy, d/M/yyyy HHmm, yyyy-MM-dd HH:mm, "
                + "d MMM yyyy h:mm a, or an ISO date-time!");
    }

    /** Builds an event after ensuring any known dates remain in chronological order. */
    private Event createUpdatedEvent(Event event) throws InvalidTaskFormatException {
        String from = edits.getOrDefault(EditField.FROM, event.getFrom());
        String to = edits.getOrDefault(EditField.TO, event.getTo());
        if (!Event.hasValidDateOrder(from, to)) {
            throw new InvalidTaskFormatException(
                    "The event ends before it starts. Make its '/to' date or time the same as or after '/from'.");
        }
        return new Event(editedDescription(event), from, to);
    }

    /** Returns the edited description or the selected task's existing description. */
    private String editedDescription(Task task) {
        return edits.getOrDefault(EditField.DESCRIPTION, task.getDescription());
    }

    /** Rejects the first field that is not available for the selected task type. */
    private void requireSupportedFields(Set<EditField> supportedFields, String message)
            throws InvalidTaskFormatException {
        for (EditField field : edits.keySet()) {
            if (!supportedFields.contains(field)) {
                throw new InvalidTaskFormatException(message);
            }
        }
    }

    /** Copies a completed task's state to its replacement. */
    private void copyCompletionState(Task originalTask, Task updatedTask) {
        if (originalTask.isDone()) {
            updatedTask.markAsDone();
        }
    }
}
