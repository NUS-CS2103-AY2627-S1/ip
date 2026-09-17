package megatron.command;

import java.util.Objects;

import megatron.exception.MegatronException;
import megatron.exception.StorageException;
import megatron.exception.TaskListFullException;
import megatron.storage.TaskStorage;
import megatron.task.Task;
import megatron.task.TaskList;
import megatron.ui.Ui;

/**
 * Adds one task to the task list.
 */
public final class AddCommand extends Command {
    private static final int MAX_TASKS = 100;

    private final String input;
    private final Parser parser;

    /**
     * Creates an add command from the original user input.
     *
     * @param input The original user input.
     * @param parser The parser used to create the task.
     */
    public AddCommand(String input, Parser parser) {
        this.input = Objects.requireNonNull(input);
        this.parser = Objects.requireNonNull(parser);
    }

    /**
     * Validates, adds, saves, and displays the new task.
     *
     * @param tasks The current task list.
     * @param ui The user interface used for output.
     * @param storage The task storage used for persistence.
     * @throws MegatronException If the task cannot be created or the list is full.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, TaskStorage storage) throws MegatronException {
        if (tasks.size() == MAX_TASKS) {
            throw new TaskListFullException();
        }
        Task task = parser.createTask(input);
        tasks.add(task);
        try {
            storage.save(tasks);
        } catch (StorageException exception) {
            tasks.removeTask(tasks.size());
            throw exception;
        }
        ui.showTaskAdded(task, tasks.size());
    }
}
