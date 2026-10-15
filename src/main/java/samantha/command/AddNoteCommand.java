package samantha.command;

import samantha.exception.NoteFileWriteException;
import samantha.exception.TaskValidationException;
import samantha.model.Note;

/**
 * Represents the command that adds a note.
 */
public class AddNoteCommand extends Command {
    private final String content;

    /**
     * Creates a command for the supplied note content.
     *
     * @param content note content
     */
    public AddNoteCommand(String content) {
        this.content = content;
    }

    /**
     * Indicates that adding this note can be undone.
     *
     * @return {@code true}.
     */
    @Override
    public boolean isUndoable() {
        return true;
    }

    /**
     * Adds and saves the note.
     *
     * @param context current application state and persistence handlers
     * @return confirmation for the newly added note
     * @throws TaskValidationException if the content is invalid
     * @throws NoteFileWriteException if saving fails
     */
    @Override
    public String execute(CommandContext context)
            throws TaskValidationException, NoteFileWriteException {
        return addAndSaveNote(new Note(content), context.getNotes(), context.getNoteStorage());
    }
}
