package rem.command;

import java.io.IOException;

import rem.exception.InvalidTaskNumberException;
import rem.exception.MissingNoteException;
import rem.storage.Storage;
import rem.task.Task;
import rem.task.TaskList;
import rem.ui.Ui;

/**
 * Removes a task's note.
 */
public class DeleteNoteCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that removes a selected task's note.
     *
     * @param taskNumber One-based number of the task to update.
     */
    public DeleteNoteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Removes the note, saves the task list, and displays the result.
     *
     * @param tasks Tasks managed by Rem.
     * @param ui User interface used to display the result.
     * @param storage Storage used to save the updated task list.
     * @throws IOException If the updated task list cannot be saved.
     * @throws InvalidTaskNumberException If the task number does not exist.
     * @throws MissingNoteException If the selected task has no note.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage)
            throws IOException, InvalidTaskNumberException, MissingNoteException {
        Task task = tasks.deleteNote(taskNumber);
        storage.saveTasks(tasks.getTasks());
        ui.showMessages("Okay. Took the note off this task:", task.toString());
    }
}
