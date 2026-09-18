package vega;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class NoteListTest {
    @Test
    public void add_validNote_increasesSizeAndStoresNote() {
        NoteList notes = new NoteList();
        Note note = new Note("room code is 1234");

        notes.add(note);

        assertEquals(1, notes.size());
        assertSame(note, notes.asList().get(0));
    }

    @Test
    public void delete_existingNote_removesAndReturnsNote() throws VegaException {
        NoteList notes = new NoteList();
        Note note = new Note("room code is 1234");
        notes.add(note);

        Note deletedNote = notes.delete(1);

        assertSame(note, deletedNote);
        assertEquals(0, notes.size());
    }

    @Test
    public void delete_outOfRangeNoteNumber_throwsVegaException() {
        NoteList notes = new NoteList();

        assertThrows(VegaException.class, () -> notes.delete(1));
    }
}
