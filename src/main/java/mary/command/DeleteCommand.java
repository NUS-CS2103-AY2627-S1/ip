package mary.command;

import java.util.List;

import mary.exception.MaryException;
import mary.storage.Storage;
import mary.task.Task;
import mary.task.TaskList;
import mary.ui.Ui;

/**
 * Removes a task by its one-based list number.
 */
public class DeleteCommand extends Command {
    private final String fullCommand;

    /**
     * Creates a command retaining the supplied input for execution-time validation.
     *
     * @param fullCommand complete, non-null user input.
     */
    public DeleteCommand(String fullCommand) {
        this.fullCommand = fullCommand;
    }

    /**
     * Removes the selected one-based task number, saves the list, and displays confirmation.
     * Invalid numbers and storage errors leave the original list unchanged.
     *
     * @param tasks current in-memory task list.
     * @param ui user-facing message handler.
     * @param storage persistence service (unused by read-only commands).
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        String numberText = fullCommand.startsWith("delete ")
                ? fullCommand.substring(7).trim() : "";
        try {
            if (numberText.isEmpty()) {
                throw new MaryException("use 'delete N', where N is a task number.");
            }
            if (!numberText.matches("[0-9]+")) {
                throw new NumberFormatException();
            }
            int index = Integer.parseInt(numberText) - 1;
            if (index < 0 || index >= tasks.size()) {
                throw new MaryException("task " + numberText
                        + " does not exist; use 'list' to see valid task numbers.");
            }
            List<Task> proposed = tasks.getTasks();
            Task removed = proposed.remove(index);
            storage.save(proposed);
            tasks.replaceWith(proposed);
            ui.showMessages(" Whisked away! I've removed this task:",
                    "   " + removed,
                    " Tasks on your list: " + tasks.size() + ".");
        } catch (NumberFormatException exception) {
            ui.showError("'" + numberText + "' is not a valid task number; use a positive whole number.");
        } catch (MaryException exception) {
            ui.showError(exception.getMessage());
        }
    }
}
