package tangent.command;

import java.util.ArrayList;
import java.util.List;

import tangent.exception.TangentException;
import tangent.storage.Storage;
import tangent.task.Task;
import tangent.task.TaskList;
import tangent.ui.Ui;

/** Changes one or more tasks' completion statuses and saves the resulting task list. */
public abstract class StatusCommand extends Command {
    /** Zero-based indexes of tasks whose statuses are changed. */
    private final List<Integer> taskIndexes;

    /** Creates a status command for the supplied 0-based task index. */
    protected StatusCommand(int taskIndex) {
        this(List.of(taskIndex));
    }

    /** Creates a status command for the supplied 0-based task indexes. */
    protected StatusCommand(List<Integer> taskIndexes) {
        this.taskIndexes = List.copyOf(taskIndexes);
    }

    /** Returns the completion status this command is expected to apply. */
    protected abstract boolean targetStatus();

    /** Displays the confirmation after successful status changes. */
    protected abstract void showConfirmation(Ui ui, List<Task> changedTasks, List<Task> unchangedTasks);

    /**
     * Changes a task's status and saves the change in the storage.
     *
     * @throws TangentException if the task index is invalid or saving fails
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws TangentException {
        List<Integer> resolvedIndexes = tasks.resolveDisplayedIndexes(taskIndexes);
        List<Task> selectedTasks = tasks.getTasksAtIndexes(resolvedIndexes);
        List<Task> changedTasks = selectedTasks.stream().filter(task -> task.isDone() != targetStatus()).toList();
        List<Task> unchangedTasks = selectedTasks.stream().filter(task -> task.isDone() == targetStatus()).toList();
        if (changedTasks.isEmpty()) {
            if (taskIndexes.size() == 1) {
                ui.showSingleStatusAlreadyApplied(targetStatus());
            } else {
                ui.showStatusAlreadyApplied(targetStatus());
            }
            return;
        }
        List<Boolean> previousStatuses = new ArrayList<>();
        for (Task task : changedTasks) {
            previousStatuses.add(task.isDone());
        }
        for (Task task : changedTasks) {
            setStatus(task, targetStatus());
        }
        try {
            storage.save(tasks.toList());
        } catch (TangentException e) {
            for (int i = 0; i < changedTasks.size(); i++) {
                setStatus(changedTasks.get(i), previousStatuses.get(i));
            }
            throw e;
        }
        showConfirmation(ui, changedTasks, unchangedTasks);
    }

    /** Applies a completion status to a task. */
    private void setStatus(Task task, boolean isDone) {
        if (isDone) {
            task.markAsDone();
        } else {
            task.markAsUndone();
        }
    }
}
