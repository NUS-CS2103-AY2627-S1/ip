package bob.command;

import java.util.Optional;

import bob.BobException;
import bob.storage.Storage;
import bob.task.Task;
import bob.task.TaskEdit;
import bob.task.TaskList;
import bob.ui.Ui;

/**
 * Changes some of the details of one task, leaving it where it is in the list.
 *
 * <p>The edited task takes the place of the original rather than being added at
 * the end, so it keeps its number, and {@link Task#withEdit} keeps its done status.
 * That is the difference from deleting a task and adding it again, which is what a
 * user had to do before this command existed.
 *
 * <p>Both the task and its edited copy are shown back. Only the details the user
 * typed change, so showing the result alone would leave them to remember what the
 * rest used to be in order to see what happened.
 *
 * <p>Which changes suit which kind of task is not decided here. The task refuses a
 * change it cannot take, since it is the one that knows which dates it has.
 */
public class EditCommand extends TaskNumberCommand {

    /** The changes to make, already read from what the user typed. */
    private final TaskEdit edit;

    /**
     * Creates a command that will edit one task.
     *
     * @param taskNumber the number the user typed, counting from 1.
     * @param edit       the changes to make to that task.
     */
    public EditCommand(int taskNumber, TaskEdit edit) {
        super(taskNumber, CommandWord.EDIT);
        this.edit = edit;
    }

    /**
     * Replaces the numbered task with its edited copy, shows the task as it was and
     * as it now is, and saves the changed list.
     *
     * <p>The copy is built before the list is touched, so an edit the task refuses
     * leaves both the list and the save file exactly as they were.
     *
     * <p>An edit that would make the task the same as another task in the list is
     * refused as well, as adding that other task a second time would be. The copy is
     * not compared with the task it was made from, so an edit that only changes the
     * capitals of a description is carried out.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BobException {
        int index = requireTaskIndex(tasks);
        Task original = tasks.get(index);
        Task edited = original.withEdit(edit);
        Optional<Integer> duplicateIndex = tasks.findDuplicate(edited, index);
        if (duplicateIndex.isPresent()) {
            int otherIndex = duplicateIndex.get();
            // The user counts from 1, the list counts from 0.
            throw new BobException("Dat change would make it da same as task " + (otherIndex + 1) + ":"
                    + "\n  " + tasks.get(otherIndex));
        }
        tasks.set(index, edited);
        ui.showEditedTask(original, edited);
        storage.save(tasks.asList());
    }
}
