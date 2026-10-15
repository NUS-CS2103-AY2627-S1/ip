package niulai.command;

import java.io.IOException;
import java.util.Objects;

import niulai.NiuLaiException;
import niulai.model.DuplicateTaskException;
import niulai.model.Task;
import niulai.model.TaskList;
import niulai.service.Storage;
import niulai.service.Ui;

/**
 * Adds a task and persists the updated task list.
 */
public class AddCommand extends Command {
    /** The task to add. */
    private final Task task;

    /**
     * Creates an add command for a task.
     *
     * @param task the task to add
     */
    public AddCommand(Task task) {
        super(getCommandType(task));
        this.task = Objects.requireNonNull(task, "task");
    }

    /**
     * Adds the task, saves the updated list, and removes the task if saving fails.
     *
     * @param tasks the current task list
     * @param ui the user-interface component
     * @param storage the task storage component
     * @throws NiuLaiException if the updated list cannot be saved
     */
    @Override
    public UndoAction execute(TaskList tasks, Ui ui, Storage storage) throws NiuLaiException {
        try {
            tasks.add(task);
        } catch (DuplicateTaskException e) {
            throw new NiuLaiException("NOOO!!! " + e.getMessage());
        }
        int taskIndex = tasks.size() - 1;
        try {
            storage.save(tasks);
        } catch (IOException | SecurityException e) {
            Task removedTask = tasks.remove(taskIndex);
            // A failed save must undo precisely the task added by this command.
            assert removedTask == task
                    : "A failed save must remove the task just added to restore the previous list.";
            throw createStorageFailure(e);
        }
        ui.showTaskAdded(task, tasks.size());
        return (currentTasks, currentStorage) -> {
            Task removedTask = currentTasks.remove(taskIndex);
            try {
                currentStorage.save(currentTasks);
            } catch (IOException | SecurityException e) {
                currentTasks.add(taskIndex, removedTask);
                throw createStorageFailure(e);
            }
        };
    }

    /** Maps the parsed task type to its corresponding user command type. */
    private static Type getCommandType(Task task) {
        return switch (Objects.requireNonNull(task, "task").getType()) {
            case TODO -> Type.TODO;
            case DEADLINE -> Type.DEADLINE;
            case EVENT -> Type.EVENT;
            default -> throw new IllegalArgumentException("Unsupported task type.");
        };
    }
}
