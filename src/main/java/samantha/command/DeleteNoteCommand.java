package samantha.command;

import samantha.exception.InputException;
import samantha.exception.NoteFileWriteException;
import samantha.model.Note;

/**
 * Represents the command that removes a note.
 */
public class DeleteNoteCommand extends Command {
    private final int noteId;
    private Note deletedNote;
    private int deletedNoteIndex;

    /**
     * Creates a command for the given one-based note ID.
     *
     * @param noteId note to remove
     */
    public DeleteNoteCommand(int noteId) {
        this.noteId = noteId;
    }

    /**
     * Indicates that deleting this note can be undone.
     *
     * @return {@code true}.
     */
    @Override
    public boolean isUndoable() {
        return true;
    }

    /**
     * Removes and saves the selected note.
     *
     * @param context current application state and persistence handlers.
     * @return confirmation for the removed note
     * @throws InputException if the note ID does not exist
     * @throws NoteFileWriteException if saving fails
     */
    @Override
    public String execute(CommandContext context) throws InputException, NoteFileWriteException {
        Note note = getNote(context.getNotes(), noteId);
        deletedNote = context.getNotes().removeNote(noteId);
        deletedNoteIndex = noteId - 1;
        saveNotes(context.getNotes(), context.getNoteStorage());
        return "I've taken that off the list:\n  " + note + "\n"
                + String.format("Now you have %d notes in the list.", context.getNotes().size());
    }

    /**
     * Restores the note deleted by this command.
     *
     * @param context current application state and persistence handlers
     * @return confirmation for the undone command.
     * @throws InputException if the deleted note has not been recorded.
     * @throws NoteFileWriteException if the note list cannot be saved.
     */
    @Override
    public String undo(CommandContext context) throws InputException, NoteFileWriteException {
        if (deletedNote == null) {
            throw new InputException("This command cannot be undone.");
        }
        context.getNotes().addNoteAt(deletedNote, deletedNoteIndex);
        saveNotes(context.getNotes(), context.getNoteStorage());
        return "I've undone the last command.";
    }
}
