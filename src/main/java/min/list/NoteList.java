package min.list;

import java.util.List;

import min.note.Note;

/**
 * Stores and updates Min's notes.
 */
public class NoteList extends ItemList<Note> {
    /**
     * Creates a note list whose initial displayed view contains every note.
     *
     * @param notes The initial notes.
     */
    public NoteList(List<Note> notes) {
        super(notes);
    }

    /**
     * Adds a note to the list.
     *
     * @param note The note to add.
     */
    public void addNote(Note note) {
        add(note);
    }

    /**
     * Sets the displayed view to notes whose text contains the given keyword.
     *
     * @param keyword The text to match against note text.
     * @return A read-only list of matching notes.
     */
    public List<Note> showMatchingNotes(String keyword) {
        assert keyword != null && !keyword.isBlank()
                : "Find keyword must not be blank.";

        return showMatching(note -> note.getText().contains(keyword));
    }

    /** Restores and returns the complete note list as the displayed view. */
    public List<Note> showAllNotes() {
        return showAll();
    }

    /**
     * Removes and returns the displayed note at the given index.
     *
     * @param index The zero-based index in the displayed note view.
     * @return The removed note.
     */
    public Note deleteNote(int index) {
        return delete(index);
    }

    /** Returns the number of notes in the current displayed view. */
    public int getDisplayedNoteCount() {
        return getDisplayedCount();
    }

    /** Returns a read-only snapshot of the current notes. */
    public List<Note> getNotes() {
        return getItems();
    }
}
