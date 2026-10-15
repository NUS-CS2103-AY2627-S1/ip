package windy.command;

import windy.task.TaskList;

/**
 * Updates and persists the completion status of one task.
 */
abstract class TaskCompletionCommand extends Command {
    private final int taskIndex;
    private final boolean shouldMarkDone;

    /**
     * Creates a command that assigns a completion status to a task.
     *
     * @param taskIndex the zero-based index of the task to update.
     * @param shouldMarkDone whether the task should be marked as completed.
     */
    TaskCompletionCommand(int taskIndex, boolean shouldMarkDone) {
        this.taskIndex = taskIndex;
        this.shouldMarkDone = shouldMarkDone;
    }

    /** {@inheritDoc} */
    @Override
    public final void execute(CommandContext context) {
        TaskList tasks = context.getTasks();
        assert taskIndex >= 0 && taskIndex < tasks.getNumTasks()
                : "Task index should have been validated by Parser";
        if (!context.applyTaskChange(candidate -> candidate.getTask(taskIndex).setDone(shouldMarkDone))) {
            return;
        }
        if (shouldMarkDone) {
            context.getUi().showTaskMarkedDone(tasks.getTask(taskIndex));
        } else {
            context.getUi().showTaskMarkedNotDone(tasks.getTask(taskIndex));
        }
    }
}
