package emma.command;

import emma.EmmaException;
import emma.Storage;
import emma.Task;
import emma.TaskList;

/**
 * Removes a numbered task from the list.
 */
public class DeleteCommand extends TaskNumberCommand {

    /**
     * Creates the command.
     *
     * @param taskNumber the 1-based task number the user gave.
     */
    public DeleteCommand(int taskNumber) {
        super(taskNumber);
    }

    @Override
    public String execute(TaskList tasks, Storage storage) throws EmmaException {
        int sizeBefore = tasks.size();
        Task task;
        try {
            task = tasks.delete(getTaskNumber());
        } catch (IndexOutOfBoundsException e) {
            throw buildNoSuchTaskError();
        }
        assert tasks.size() == sizeBefore - 1 : "a delete should remove exactly one task";
        storage.saveOrUndo(tasks, () -> tasks.insert(getTaskNumber(), task));
        return "Okay, I've removed this:\n  " + task;
    }
}
