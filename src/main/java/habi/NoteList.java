package habi;

import java.util.ArrayList;
import java.util.List;

/** Owns the note collection and provides operations that change it. */
public class NoteList {
    private final ArrayList<Note> notes;

    /** Creates an empty note list. */
    public NoteList() {
        this(new ArrayList<>());
    }

    /**
     * Creates a note list containing notes loaded from storage.
     *
     * @param notes Notes to place in the list.
     */
    public NoteList(ArrayList<Note> notes) {
        assert notes != null : "note collection must not be null";
        this.notes = notes;
    }

    /**
     * Adds a note to the end of the list.
     *
     * @param note Note to add.
     */
    public void add(Note note) {
        assert note != null : "note must not be null";
        notes.add(note);
    }

    /**
     * Removes and returns the note at a zero-based index.
     *
     * @param index Zero-based note index.
     * @return The removed note.
     */
    public Note delete(int index) {
        return notes.remove(index);
    }

    /**
     * Returns the number of notes in the list.
     *
     * @return Note count.
     */
    public int size() {
        return notes.size();
    }

    /**
     * Returns an unmodifiable snapshot of the current notes.
     *
     * @return Current notes in list order.
     */
    public List<Note> asList() {
        return List.copyOf(notes);
    }
}
