package samantha.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import samantha.exception.InputException;

class NoteListTest {
    @Test
    void noteOperations_preserveOrderAndUseOneBasedNoteIds() throws Exception {
        NoteList notes = new NoteList();
        Note first = new Note("first");
        Note second = new Note("second");
        Note third = new Note("third");

        notes.add(first);
        notes.addAll(java.util.List.of(second, third));

        assertEquals(first, notes.getNote(1));
        assertEquals(third, notes.getNote(3));
        assertEquals(second, notes.removeNote(2));
        assertEquals(java.util.List.of(first, third), notes.asList());
    }

    @Test
    void getNote_invalidOneBasedId_inputExceptionThrown() throws Exception {
        NoteList notes = new NoteList();
        notes.add(new Note("only note"));

        assertThrows(InputException.class, () -> notes.getNote(0));
        assertThrows(InputException.class, () -> notes.getNote(-1));
        assertThrows(InputException.class, () -> notes.getNote(2));
    }
}
