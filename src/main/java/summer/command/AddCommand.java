package summer.command;

import summer.SummerException;
import summer.parser.Parser;
import summer.storage.Storage;
import summer.task.Task;
import summer.task.TaskList;
import summer.ui.Ui;

/**
 * Adds a new task (todo, deadline, or event).
 *
 * <p>Parsing is deferred to {@link #execute}, after the capacity check, so a
 * malformed command while the list is full still reports "list is full"
 * rather than a syntax error (matches Summer's original behavior).
 */
public class AddCommand extends Command {
    private final String rawCommand;

    /**
     * Creates the command.
     *
     * @param rawCommand the full command line; the task it describes is
     *                   parsed when this command executes
     */
    public AddCommand(String rawCommand) {
        this.rawCommand = rawCommand;
    }

    /** Parses the task from the command line and adds it, unless the list is full. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws SummerException {
        if (tasks.isFull()) {
            ui.showMessage(" Board's full, only room for 100 tasks out here.");
            return;
        }
        Task task = Parser.createTask(this.rawCommand);
        // createTask either returns a task or throws SummerException; it never
        // returns null.
        assert task != null : "parser returned a null task";

        tasks.add(task);
        storage.save(tasks);
        ui.showTaskAdded(task, tasks.size());
    }
}
