package rem.command;

import java.io.IOException;

import rem.exception.InvalidTaskNumberException;
import rem.storage.Storage;
import rem.task.Task;
import rem.task.TaskList;
import rem.ui.Ui;

/**
 * Adds or replaces a task's note.
 */
public class NoteCommand extends Command {
    private final int taskNumber;
    private final String note;

    /**
     * Creates a command that updates a selected task's note.
     *
     * @param taskNumber One-based number of the task to update.
     * @param note Valid, trimmed note text.
     */
    public NoteCommand(int taskNumber, String note) {
        this.taskNumber = taskNumber;
        this.note = note;
    }

    /**
     * Updates the note, saves the task list, and displays the result.
     *
     * @param tasks Tasks managed by Rem.
     * @param ui User interface used to display the result.
     * @param storage Storage used to save the updated task list.
     * @throws IOException If the updated task list cannot be saved.
     * @throws InvalidTaskNumberException If the task number does not exist.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage)
            throws IOException, InvalidTaskNumberException {
        Task task = tasks.setNote(taskNumber, note);
        storage.saveTasks(tasks.getTasks());
        ui.showMessages("I'll keep this note with your task:", task.toString());
    }
}
