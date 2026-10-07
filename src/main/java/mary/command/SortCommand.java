package mary.command;

import mary.exception.MaryException;
import mary.storage.Storage;
import mary.task.TaskList;
import mary.ui.Ui;

/**
 * Reorders dated tasks chronologically and persists their new list numbers.
 */
public class SortCommand extends Command {
    private final String arguments;

    /**
     * Retains arguments so unsupported options can receive specific usage guidance.
     *
     * @param arguments text after the sort command word.
     */
    public SortCommand(String arguments) {
        this.arguments = arguments;
    }

    /**
     * Sorts and saves nonempty lists, then displays the new task numbers.
     * A save failure reports an error and preserves the original list order.
     *
     * @param tasks current task list.
     * @param ui destination for confirmations and errors.
     * @param storage persistence service for the reordered list.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        if (!arguments.isBlank()) {
            ui.showError("use 'sort' without arguments to order tasks chronologically.");
            return;
        }
        if (tasks.isEmpty()) {
            ui.showMessage(" Nothing to chase yet! Add a task with 'todo description'.");
            return;
        }
        TaskList proposed = new TaskList(tasks.getTasks());
        proposed.sortChronologically();
        try {
            storage.save(proposed.getTasks());
            tasks.replaceWith(proposed.getTasks());
            ui.showMessage(" Tasks lined up chronologically "
                    + "(deadlines by due time, events by start time; todos last).");
            new ListCommand().execute(tasks, ui, storage);
        } catch (MaryException exception) {
            ui.showError(exception.getMessage());
        }
    }
}
