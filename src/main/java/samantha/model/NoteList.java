package samantha.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import samantha.exception.InputException;

/**
 * Stores and provides operations on Samantha's notes.
 */
public class NoteList {
    private final List<Note> notes;

    /**
     * Creates an empty note list.
     */
    public NoteList() {
        this.notes = new ArrayList<>();
    }

    /**
     * Adds all supplied notes to this note list.
     *
     * @param notes notes to add
     */
    public void addAll(Collection<Note> notes) {
        assert notes != null : "Notes to add must not be null";
        assert notes.stream().noneMatch(note -> note == null)
                : "A note list must not contain null notes";
        this.notes.addAll(notes);
    }

    /**
     * Adds a note to the end of this note list.
     *
     * @param note note to add
     */
    public void add(Note note) {
        assert note != null : "A note list must not contain null notes";
        notes.add(note);
    }

    /**
     * Inserts a note at a zero-based position in this note list.
     *
     * @param note note to insert.
     * @param index zero-based insertion position.
     */
    public void addNoteAt(Note note, int index) {
        assert note != null : "A note list must not contain null notes";
        assert index >= 0 && index <= notes.size() : "Note index must be within insertion range";
        notes.add(index, note);
    }

    /**
     * Returns the note identified by a one-based user-facing ID.
     *
     * @param noteId one-based note ID
     * @return the matching note
     * @throws InputException if the ID is outside this list
     */
    public Note getNote(int noteId) throws InputException {
        return notes.get(getNoteIndex(noteId));
    }

    /**
     * Removes and returns the note identified by a one-based user-facing ID.
     *
     * @param noteId one-based note ID
     * @return the removed note
     * @throws InputException if the ID is outside this list
     */
    public Note removeNote(int noteId) throws InputException {
        return notes.remove(getNoteIndex(noteId));
    }

    /**
     * Removes a specific note from this note list.
     *
     * @param note note to remove.
     * @throws InputException if the note is not in this note list.
     */
    public void removeNote(Note note) throws InputException {
        assert note != null : "A note to remove must not be null";
        if (!notes.remove(note)) {
            throw new InputException("You entered a note number that does not exist.");
        }
    }

    /**
     * Returns the number of notes in this list.
     *
     * @return the note count
     */
    public int size() {
        return notes.size();
    }

    /**
     * Returns an immutable snapshot of the current notes for persistence.
     *
     * @return snapshot of the notes in this list
     */
    public List<Note> asList() {
        return List.copyOf(notes);
    }

    /**
     * Converts a valid one-based note ID into the list's internal index.
     *
     * @param noteId one-based note ID
     * @return zero-based index for the note ID
     * @throws InputException if the ID is outside this list
     */
    private int getNoteIndex(int noteId) throws InputException {
        if (noteId < 1 || noteId > notes.size()) {
            throw new InputException("You entered a note number that does not exist.");
        }
        return noteId - 1;
    }
}
