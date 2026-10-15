package samantha.command;

import samantha.exception.InputException;
import samantha.exception.NoteFileWriteException;
import samantha.exception.TaskFileWriteException;
import samantha.exception.TaskValidationException;
import samantha.model.Note;
import samantha.model.NoteList;
import samantha.model.Task;
import samantha.model.TaskList;
import samantha.storage.NoteStorage;
import samantha.storage.Storage;
import samantha.ui.Ui;

/**
 * Represents an executable user command.
 */
public abstract class Command {
    /** The task added by an add command, when this command is undoable. */
    private Task addedTask;
    /** The note added by an add command, when this command is undoable. */
    private Note addedNote;

    /**
     * Creates a command.
     */
    public Command() {
    }

    /**
     * Performs this command and returns its user-facing response.
     *
     * @param context current application state and persistence handlers
     * @return response for the command
     * @throws TaskValidationException if task data is invalid
     * @throws InputException if a task value has invalid input format
     * @throws TaskFileWriteException if the task list cannot be saved
     * @throws NoteFileWriteException if the note list cannot be saved
     */
    public abstract String execute(CommandContext context)
            throws TaskValidationException, InputException, TaskFileWriteException,
            NoteFileWriteException;

    /**
     * Performs this command using the legacy task-only command interface.
     *
     * @param tasks current task list
     * @param storage task persistence handler
     * @return response for the command
     * @throws TaskValidationException if task data is invalid
     * @throws InputException if a task value has invalid input format
     * @throws TaskFileWriteException if the task list cannot be saved
     * @throws NoteFileWriteException if the note list cannot be saved
     */
    public String execute(TaskList tasks, Storage storage)
            throws TaskValidationException, InputException, TaskFileWriteException,
            NoteFileWriteException {
        return execute(new CommandContext(tasks, storage));
    }

    /**
     * Performs this command and displays its response through the console UI.
     *
     * @param tasks current task list
     * @param ui console interaction handler
     * @param storage task persistence handler
     * @throws TaskValidationException if task data is invalid
     * @throws InputException if a task value has invalid input format
     * @throws TaskFileWriteException if the task list cannot be saved
     * @throws NoteFileWriteException if the note list cannot be saved
     */
    public void execute(TaskList tasks, Ui ui, Storage storage)
            throws TaskValidationException, InputException, TaskFileWriteException,
            NoteFileWriteException {
        String response = execute(tasks, storage);
        if (!response.isEmpty()) {
            ui.showResponse(response);
        }
    }

    /**
     * Performs this command and displays its response through the console UI.
     *
     * @param context current application state and persistence handlers
     * @param ui console interaction handler
     * @throws TaskValidationException if task data is invalid
     * @throws InputException if a task value has invalid input format
     * @throws TaskFileWriteException if the task list cannot be saved
     * @throws NoteFileWriteException if the note list cannot be saved
     */
    public void execute(CommandContext context, Ui ui)
            throws TaskValidationException, InputException, TaskFileWriteException,
            NoteFileWriteException {
        String response = execute(context);
        if (!response.isEmpty()) {
            ui.showResponse(response);
        }
    }

    /**
     * Returns whether executing this command should end the application.
     *
     * @return {@code true} only for an exit command
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Returns whether this command can be undone.
     *
     * @return {@code true} when this command changes application state.
     */
    public boolean isUndoable() {
        return false;
    }

    /**
     * Returns whether this command requests undoing the latest command.
     *
     * @return {@code true} only for the undo command
     */
    public boolean isUndo() {
        return false;
    }

    /**
     * Reverses this command.
     *
     * @param context current application state and persistence handlers.
     * @return confirmation for the undone command.
     * @throws InputException if the command cannot be undone.
     * @throws TaskValidationException if restored note content is invalid.
     * @throws TaskFileWriteException if the task list cannot be saved.
     * @throws NoteFileWriteException if the note list cannot be saved.
     */
    public String undo(CommandContext context)
            throws InputException, TaskValidationException, TaskFileWriteException,
            NoteFileWriteException {
        if (addedTask != null) {
            context.getTasks().removeTask(addedTask);
            saveTasks(context.getTasks(), context.getTaskStorage());
            return "I've undone the last command.";
        }
        if (addedNote != null) {
            context.getNotes().removeNote(addedNote);
            saveNotes(context.getNotes(), context.getNoteStorage());
            return "I've undone the last command.";
        }
        throw new InputException("This command cannot be undone.");
    }

    /**
     * Saves the current task list through the storage component.
     *
     * @param tasks task list to save
     * @param storage task persistence handler
     * @throws TaskFileWriteException if the task list cannot be saved
     */
    protected void saveTasks(TaskList tasks, Storage storage) throws TaskFileWriteException {
        storage.save(tasks.asList());
    }

    /**
     * Adds a task, persists the updated list, and returns its confirmation.
     *
     * @param task task to add
     * @param tasks current task list
     * @param storage task persistence handler
     * @return confirmation for the added task
     * @throws TaskFileWriteException if the task list cannot be saved
     */
    protected String addAndSaveTask(Task task, TaskList tasks, Storage storage)
            throws TaskFileWriteException {
        tasks.add(task);
        saveTasks(tasks, storage);
        addedTask = task;
        return getTaskAddedResponse(task, tasks.size());
    }

    /**
     * Returns the confirmation after a new task is added.
     *
     * @param task newly added task
     * @param taskCount number of tasks after the addition
     * @return confirmation for the added task
     */
    protected String getTaskAddedResponse(Task task, int taskCount) {
        return "I'll remember that:\n  "
                + task + "\n"
                + String.format("Now you have %d tasks in the list.", taskCount);
    }

    /**
     * Returns the task for a one-based task ID after validating its range.
     *
     * @param tasks current task list
     * @param taskId one-based task ID
     * @return the matching task
     * @throws InputException if the task ID is outside the task list
     */
    protected Task getTask(TaskList tasks, int taskId) throws InputException {
        return tasks.getTask(taskId);
    }

    /**
     * Adds a note, persists the updated list, and returns its confirmation.
     *
     * @param note note to add
     * @param notes current note list
     * @param storage note persistence handler
     * @return confirmation for the added note
     * @throws NoteFileWriteException if the note list cannot be saved
     */
    protected String addAndSaveNote(Note note, NoteList notes, NoteStorage storage)
            throws NoteFileWriteException {
        notes.add(note);
        saveNotes(notes, storage);
        addedNote = note;
        return "I'll keep this note:\n  "
                + note + "\n"
                + String.format("Now you have %d notes in the list.", notes.size());
    }

    /**
     * Saves the current note list through the note storage component.
     *
     * @param notes note list to save
     * @param storage note persistence handler
     * @throws NoteFileWriteException if the note list cannot be saved
     */
    protected void saveNotes(NoteList notes, NoteStorage storage) throws NoteFileWriteException {
        storage.save(notes.asList());
    }

    /**
     * Returns the note for a one-based note ID after validating its range.
     *
     * @param notes current note list
     * @param noteId one-based note ID
     * @return the matching note
     * @throws InputException if the note ID is outside the note list
     */
    protected Note getNote(NoteList notes, int noteId) throws InputException {
        return notes.getNote(noteId);
    }
}
