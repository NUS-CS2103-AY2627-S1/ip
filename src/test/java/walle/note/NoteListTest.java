package walle.note;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class NoteListTest {

    @Test
    public void add_singleNote_sizeIncreasesAndNoteRetrievable() {
        NoteList noteList = new NoteList();
        Note note = new Note("waist size 32");

        noteList.add(note);

        assertEquals(1, noteList.size());
        assertSame(note, noteList.getNotes().get(0));
    }

    @Test
    public void delete_firstOfTwoNotes_removesNoteAndShiftsRemaining() {
        NoteList noteList = new NoteList();
        Note first = new Note("first note");
        Note second = new Note("second note");
        noteList.add(first);
        noteList.add(second);

        Note deleted = noteList.delete(1);

        assertSame(first, deleted);
        assertEquals(1, noteList.size());
        assertSame(second, noteList.getNotes().get(0));
    }

    @Test
    public void constructor_existingNotes_exposesThoseSameNotes() {
        Note loadedNote = new Note("loaded note");
        ArrayList<Note> existingNotes = new ArrayList<>();
        existingNotes.add(loadedNote);

        NoteList noteList = new NoteList(existingNotes);

        assertEquals(1, noteList.size());
        assertSame(loadedNote, noteList.getNotes().get(0));
    }

}
