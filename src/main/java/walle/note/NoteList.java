package walle.note;

import java.util.ArrayList;

import walle.exceptions.DuplicateEntryException;
import walle.exceptions.InvalidIndexException;

/**
 * Holds the list of notes WALLE is tracking, and provides operations to
 * add, remove, and retrieve notes by their 1-based position in the list.
 */
public class NoteList {

    private final ArrayList<Note> notes;

    /**
     * Creates an empty note list.
     */
    public NoteList() {
        this.notes = new ArrayList<>();
    }

    /**
     * Creates a note list wrapping notes already loaded (e.g. from storage).
     *
     * @param notes the notes to start with.
     */
    public NoteList(ArrayList<Note> notes) {
        this.notes = notes;
    }

    /**
     * Adds a note to the end of the list.
     *
     * @param note the note to add.
     * @throws DuplicateEntryException if an identical note (same text) is
     *                                 already in the list.
     */
    public void add(Note note) {
        if (this.notes.contains(note)) {
            throw new DuplicateEntryException("note");
        }
        this.notes.add(note);
    }

    /**
     * Removes and returns the note at the given 1-based position.
     *
     * @param pos the 1-based position of the note to remove.
     * @return the note that was removed.
     * @throws InvalidIndexException if pos isn't a valid position in the list.
     */
    public Note delete(int pos) {
        if (pos < 1 || pos > this.notes.size()) {
            throw new InvalidIndexException(pos, this.notes.size(), "note");
        }
        return this.notes.remove(pos - 1);
    }

    /**
     * Returns the number of notes currently in the list.
     *
     * @return the number of notes.
     */
    public int size() {
        return this.notes.size();
    }

    /**
     * Returns the underlying list of notes, e.g. for storage or Ui display.
     *
     * @return the underlying list of notes.
     */
    public ArrayList<Note> getNotes() {
        return this.notes;
    }

}
