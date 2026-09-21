package caitlyn;

import java.util.List;

/**
 * A command that marks or unmarks one task.
 */
public final class MarkCommand extends Command {
    /** The complete mark or unmark command. */
    private final String command;

    /** Whether this command marks the selected task as done. */
    private final boolean isMarkingDone;

    /**
     * Creates a mark or unmark command.
     *
     * @param command the complete command entered by the user.
     * @param isMarkingDone whether the command should mark the task as done.
     */
    public MarkCommand(String command, boolean isMarkingDone) {
        this(command, isMarkingDone, new TaskStorage());
    }

    /**
     * Creates a mark or unmark command with supplied storage.
     *
     * @param command the complete command entered by the user.
     * @param isMarkingDone whether the task should be marked done.
     * @param storage the destination for task changes.
     */
    MarkCommand(String command, boolean isMarkingDone, TaskStorage storage) {
        super(storage);
        this.command = command;
        this.isMarkingDone = isMarkingDone;
    }

    /**
     * Changes the selected task's completion status and saves the result.
     *
     * @param tasks the current task list.
     * @param ui the UI used to display the result.
     * @throws CaitlynException when the task number is invalid or saving fails.
     */
    @Override
    public void execute(List<Task> tasks, Ui ui) throws CaitlynException {
        String commandName = isMarkingDone ? "mark" : "unmark";
        int taskIndex = parseTaskIndex(command, commandName, tasks);
        Task task = tasks.get(taskIndex);
        boolean wasDone = task.isDone();
        if (isMarkingDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        try {
            saveTasks(tasks);
        } catch (CaitlynException exception) {
            if (wasDone) {
                task.markAsDone();
            } else {
                task.markAsNotDone();
            }
            throw exception;
        }
        ui.showTaskStatus(task, isMarkingDone);
    }
}
