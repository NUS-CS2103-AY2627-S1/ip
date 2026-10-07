package mary.command;

import java.util.List;

import mary.exception.MaryException;
import mary.parser.Parser;
import mary.storage.Storage;
import mary.task.Task;
import mary.task.TaskList;
import mary.ui.Ui;

/**
 * Adds a todo, deadline, or event task.
 */
public class AddCommand extends Command {
    private final String fullCommand;

    /**
     * Creates a command retaining the supplied input for execution-time validation.
     *
     * @param fullCommand complete, non-null user input.
     */
    public AddCommand(String fullCommand) {
        this.fullCommand = fullCommand;
    }

    /**
     * Parses and adds a task, then saves the list and displays confirmation.
     * Validation and storage errors leave the original list unchanged.
     *
     * @param tasks current in-memory task list.
     * @param ui user-facing message handler.
     * @param storage persistence service (unused by read-only commands).
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        try {
            Task task = Parser.parseTask(fullCommand);
            List<Task> proposed = tasks.getTasks();
            if (proposed.stream().anyMatch(task::hasSameDetails)) {
                throw new MaryException("that task already exists; use 'list' to find it or change its details.");
            }
            proposed.add(task);
            storage.save(proposed);
            tasks.replaceWith(proposed);
            ui.showMessages(" Purr-fect! I've added this task:",
                    "   " + task,
                    " Tasks on your list: " + tasks.size() + ".");
        } catch (MaryException exception) {
            ui.showError(exception.getMessage());
        }
    }
}
