package samantha.command;

import samantha.model.NoteList;
import samantha.model.TaskList;
import samantha.storage.NoteStorage;
import samantha.storage.Storage;

/**
 * Provides the application state and persistence handlers needed by commands.
 */
public class CommandContext {
    private final TaskList tasks;
    private final NoteList notes;
    private final Storage taskStorage;
    private final NoteStorage noteStorage;

    /**
     * Creates a context for existing task commands.
     *
     * @param tasks current task list
     * @param taskStorage task persistence handler
     */
    public CommandContext(TaskList tasks, Storage taskStorage) {
        this(tasks, new NoteList(), taskStorage, new NoteStorage());
    }

    /**
     * Creates a context containing both tasks and notes.
     *
     * @param tasks current task list
     * @param notes current note list
     * @param taskStorage task persistence handler
     * @param noteStorage note persistence handler
     */
    public CommandContext(TaskList tasks, NoteList notes, Storage taskStorage, NoteStorage noteStorage) {
        assert tasks != null : "Command context must have a task list";
        assert notes != null : "Command context must have a note list";
        assert taskStorage != null : "Command context must have task storage";
        assert noteStorage != null : "Command context must have note storage";
        this.tasks = tasks;
        this.notes = notes;
        this.taskStorage = taskStorage;
        this.noteStorage = noteStorage;
    }

    /**
     * Returns the current task list.
     *
     * @return current task list
     */
    public TaskList getTasks() {
        return tasks;
    }

    /**
     * Returns the current note list.
     *
     * @return current note list
     */
    public NoteList getNotes() {
        return notes;
    }

    /**
     * Returns the task persistence handler.
     *
     * @return task storage
     */
    public Storage getTaskStorage() {
        return taskStorage;
    }

    /**
     * Returns the note persistence handler.
     *
     * @return note storage
     */
    public NoteStorage getNoteStorage() {
        return noteStorage;
    }
}
