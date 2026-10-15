package samantha.command;

import samantha.exception.InputException;
import samantha.exception.NoteFileWriteException;
import samantha.exception.TaskValidationException;
import samantha.model.Note;

/**
 * Represents the command that edits a note.
 */
public class EditNoteCommand extends Command {
    private final int noteId;
    private final String content;
    private String previousContent;

    /**
     * Creates a command for a note replacement.
     *
     * @param noteId one-based note ID
     * @param content replacement note content
     */
    public EditNoteCommand(int noteId, String content) {
        this.noteId = noteId;
        this.content = content;
    }

    /**
     * Indicates that editing this note can be undone.
     *
     * @return {@code true}.
     */
    @Override
    public boolean isUndoable() {
        return true;
    }

    /**
     * Edits and saves the selected note.
     *
     * @param context current application state and persistence handlers.
     * @return confirmation for the edited note
     * @throws InputException if the note ID does not exist
     * @throws TaskValidationException if the replacement content is invalid
     * @throws NoteFileWriteException if saving fails
     */
    @Override
    public String execute(CommandContext context)
            throws InputException, TaskValidationException, NoteFileWriteException {
        Note note = getNote(context.getNotes(), noteId);
        previousContent = note.getContent();
        note.edit(content);
        saveNotes(context.getNotes(), context.getNoteStorage());
        return "I've updated this note:\n  " + note;
    }

    /**
     * Restores the note content from before this command.
     *
     * @param context current application state and persistence handlers
     * @return confirmation for the undone command.
     * @throws InputException if the note no longer exists.
     * @throws TaskValidationException if the saved content is invalid.
     * @throws NoteFileWriteException if the note list cannot be saved.
     */
    @Override
    public String undo(CommandContext context)
            throws InputException, TaskValidationException, NoteFileWriteException {
        Note note = getNote(context.getNotes(), noteId);
        note.edit(previousContent);
        saveNotes(context.getNotes(), context.getNoteStorage());
        return "I've undone the last command.";
    }
}
