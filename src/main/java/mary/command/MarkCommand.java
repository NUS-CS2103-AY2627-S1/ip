package mary.command;

import mary.exception.MaryException;
import mary.storage.Storage;
import mary.task.Task;
import mary.task.TaskList;
import mary.ui.Ui;

/**
 * Marks a task done or not done.
 */
public class MarkCommand extends Command {
    private final String fullCommand;

    /**
     * Creates a command retaining the supplied input for execution-time validation.
     *
     * @param fullCommand complete, non-null user input.
     */
    public MarkCommand(String fullCommand) {
        this.fullCommand = fullCommand;
    }

    /**
     * Updates completion using a one-based task number and saves the list.
     * Invalid input and storage errors leave the original completion state unchanged.
     *
     * @param tasks current in-memory task list.
     * @param ui user-facing message handler.
     * @param storage persistence service (unused by read-only commands).
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        boolean isDone = fullCommand.startsWith("mark ");
        String numberText = "";
        try {
            // Validate the prefix before slicing: bare mark/unmark have no argument.
            String prefix = isDone ? "mark " : "unmark ";
            if (!fullCommand.startsWith(prefix)) {
                throw new MaryException("use 'mark N' or 'unmark N', where N is a task number.");
            }
            numberText = fullCommand.substring(prefix.length()).trim();
            if (numberText.isEmpty()) {
                throw new MaryException("use 'mark N' or 'unmark N', where N is a task number.");
            }
            if (!numberText.matches("[0-9]+")) {
                throw new NumberFormatException();
            }
            int index = Integer.parseInt(numberText) - 1;
            if (index < 0 || index >= tasks.size()) {
                throw new MaryException("task " + numberText
                        + " does not exist; use 'list' to see valid task numbers.");
            }
            Task task = tasks.get(index);
            boolean wasDone = task.getStatusIcon().equals("X");
            if (isDone) {
                task.markAsDone();
            } else {
                task.markAsNotDone();
            }
            try {
                storage.save(tasks.getTasks());
            } catch (MaryException exception) {
                task.setDone(wasDone);
                throw exception;
            }
            ui.showMessages(isDone ? " Pawsome! Task completed:"
                    : " Back on your list, ready for another pounce:", "   " + task);
        } catch (NumberFormatException exception) {
            ui.showError("'" + numberText + "' is not a valid task number; use a positive whole number.");
        } catch (MaryException exception) {
            ui.showError(exception.getMessage());
        }
    }
}
