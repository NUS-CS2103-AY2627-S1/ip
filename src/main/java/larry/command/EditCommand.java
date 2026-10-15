package larry.command;

import java.time.format.DateTimeParseException;

import larry.exception.LarryException;
import larry.storage.Storage;
import larry.task.Deadline;
import larry.task.Event;
import larry.task.Task;
import larry.task.TaskList;
import larry.ui.Ui;

/**
 * Changes one supported field of an existing task.
 */
public class EditCommand extends Command {
    private static final String INVALID_EVENT_ORDER_MESSAGE =
            "An event must end after it starts.";

    private final int taskIndex;
    private final EditField field;
    private final String replacementValue;

    /**
     * Creates a command that changes one field of a task.
     *
     * @param taskIndex Zero-based index of the task to edit.
     * @param field Field to change.
     * @param replacementValue New value for the field.
     */
    public EditCommand(int taskIndex, EditField field, String replacementValue) {
        this.taskIndex = taskIndex;
        this.field = field;
        this.replacementValue = replacementValue;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws LarryException {
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new LarryException("There is no task at index "
                    + (taskIndex + 1) + " to edit.");
        }

        Task task = tasks.get(taskIndex);
        String previousTaskText = task.toString();
        updateTask(task);
        saveTasks(tasks, ui, storage);
        ui.showTaskEdited(taskIndex + 1, previousTaskText, task);
    }

    /**
     * Applies the requested field update after checking task compatibility.
     *
     * @param task Task to update.
     * @throws LarryException If the field is incompatible or its value is invalid.
     */
    private void updateTask(Task task) throws LarryException {
        try {
            switch (field) {
                case DESCRIPTION -> task.updateDescription(replacementValue);
                case DUE_DATE_TIME -> updateDeadline(task);
                case START_DATE_TIME -> updateEventStart(task);
                case END_DATE_TIME -> updateEventEnd(task);
                default -> throw new AssertionError("Every edit field must be handled");
            }
        } catch (DateTimeParseException e) {
            throw new LarryException("The value for " + field.getMarker()
                    + " is not a valid date and time.");
        } catch (IllegalArgumentException e) {
            throw new LarryException(INVALID_EVENT_ORDER_MESSAGE);
        }
    }

    /**
     * Updates a deadline's due date after checking its task type.
     */
    private void updateDeadline(Task task) throws LarryException {
        if (!(task instanceof Deadline deadline)) {
            throw incompatibleField(task);
        }
        deadline.updateDueDateTime(replacementValue);
    }

    /**
     * Updates an event's start date after checking its task type.
     */
    private void updateEventStart(Task task) throws LarryException {
        if (!(task instanceof Event event)) {
            throw incompatibleField(task);
        }
        event.updateStartDateTime(replacementValue);
    }

    /**
     * Updates an event's end date after checking its task type.
     */
    private void updateEventEnd(Task task) throws LarryException {
        if (!(task instanceof Event event)) {
            throw incompatibleField(task);
        }
        event.updateEndDateTime(replacementValue);
    }

    /**
     * Creates a field/type mismatch error that names both relevant values.
     */
    private LarryException incompatibleField(Task task) {
        return new LarryException("You cannot edit " + field.getMarker()
                + " on " + getTaskTypePhrase(task) + ".");
    }

    /**
     * Returns the user-facing name of a task type.
     */
    private String getTaskTypePhrase(Task task) {
        if (task instanceof Deadline) {
            return "a deadline task";
        }
        if (task instanceof Event) {
            return "an event task";
        }
        return "a todo task";
    }
}
