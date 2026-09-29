package emma.command;

import emma.EmmaException;
import emma.Storage;
import emma.Task;
import emma.TaskList;

/**
 * Marks a numbered task as done or as not done yet.
 */
public class MarkCommand extends TaskNumberCommand {

    private final boolean isDone;

    /**
     * Creates the command.
     *
     * @param taskNumber the 1-based task number the user gave.
     * @param isDone true for "mark", false for "unmark".
     */
    public MarkCommand(int taskNumber, boolean isDone) {
        super(taskNumber);
        this.isDone = isDone;
    }

    @Override
    public String execute(TaskList tasks, Storage storage) throws EmmaException {
        Task task;
        boolean wasDone;
        try {
            task = tasks.get(getTaskNumber());
            wasDone = task.isDone();
            tasks.applyMark(getTaskNumber(), isDone);
            assert task.isDone() == isDone : "marking should have left the task as asked";
        } catch (IndexOutOfBoundsException e) {
            throw buildNoSuchTaskError();
        }
        storage.saveOrUndo(tasks, () -> task.setDone(wasDone));
        String message = isDone
                ? "Nice! I've marked this as done:"
                : "Okay, I've marked this as not done yet:";
        return message + "\n  " + task;
    }
}
