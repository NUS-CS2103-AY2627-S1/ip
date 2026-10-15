package rex.command;

import rex.RexException;
import rex.TaskList;
import rex.Ui;
import rex.task.Task;

/** Marks a task as not done again. */
public class UnmarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that marks a task as not done.
     *
     * @param taskNumber the number the user gave, counting from 1.
     */
    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Marks the numbered task as not done and shows it back.
     *
     * @throws RexException if no task has that number.
     */
    @Override
    public void execute(TaskList tasks, Ui ui) throws RexException {
        Task task = tasks.getByNumber(taskNumber);
        task.markAsNotDone();
        ui.showUnmarked(task);
    }

    /** Returns true: whether a task is done is part of what gets saved. */
    @Override
    public boolean isTaskListChanged() {
        return true;
    }
}
