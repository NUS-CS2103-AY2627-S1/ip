package vega;

import java.util.ArrayList;
import java.util.List;

/** Owns Vega's note collection and validates numbered note access. */
public class NoteList {
    private final ArrayList<Note> notes;

    /** Creates an empty note list. */
    public NoteList() {
        notes = new ArrayList<>();
    }

    /**
     * Creates a note list containing notes loaded from storage.
     *
     * @param notes Notes loaded from persistent storage.
     */
    public NoteList(List<Note> notes) {
        assert notes != null : "Initial note list must not be null";
        assert notes.stream().noneMatch(note -> note == null) : "Note list must not contain null";
        this.notes = new ArrayList<>(notes);
    }

    /**
     * Adds a note to the end of the list.
     *
     * @param note Note to add.
     */
    public void add(Note note) {
        assert note != null : "Note to add must not be null";
        notes.add(note);
    }

    /**
     * Removes and returns the note at the given one-based number.
     *
     * @param noteNumber One-based position of the note.
     * @return Removed note.
     * @throws VegaException If the note number is outside the list.
     */
    public Note delete(int noteNumber) throws VegaException {
        int noteIndex = noteNumber - 1;
        if (noteIndex < 0 || noteIndex >= notes.size()) {
            throw new VegaException("There is no note numbered " + noteNumber + ".");
        }
        return notes.remove(noteIndex);
    }

    /**
     * Returns the number of notes in the list.
     *
     * @return Number of stored notes.
     */
    public int size() {
        return notes.size();
    }

    /**
     * Returns an unmodifiable snapshot for display and storage.
     *
     * @return Snapshot of the notes in their current order.
     */
    public List<Note> asList() {
        return List.copyOf(notes);
    }
}
