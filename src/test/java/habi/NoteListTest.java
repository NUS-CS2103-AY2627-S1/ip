package habi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests note-list ordering, removal, and snapshots. */
public class NoteListTest {
    @Test
    public void addAndDelete_notesRetainInsertionOrder() {
        NoteList notes = new NoteList();
        Note first = new Note("buy milk");
        Note second = new Note("call Mum");
        notes.add(first);
        notes.add(second);

        assertEquals(List.of(first, second), notes.asList());
        assertEquals(first, notes.delete(0));
        assertEquals(List.of(second), notes.asList());
    }

    @Test
    public void asList_snapshotCannotBeModified() {
        NoteList notes = new NoteList();
        notes.add(new Note("buy milk"));

        List<Note> snapshot = notes.asList();

        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new Note("call Mum")));
        assertEquals(1, notes.size());
    }
}
