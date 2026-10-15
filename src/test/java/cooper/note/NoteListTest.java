package cooper.note;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import cooper.exception.CooperException;

/** Verifies immutable candidate changes and stable search numbering. */
public class NoteListTest {
    @Test
    public void mutations_preserveOriginalAndRenumberOnlyAfterDeletion() {
        NoteList original = new NoteList(List.of(new Note("first"), new Note("second")));
        NoteList edited = original.edit(2, new Note("replacement"));
        assertEquals("second", original.get(2).text());
        assertEquals("replacement", edited.get(2).text());
        assertEquals("replacement", edited.delete(1).get(1).text());
        assertEquals(3, original.add(new Note("first")).size());
        assertThrows(UnsupportedOperationException.class, () -> original.asList().clear());
        assertThrows(CooperException.class, () -> original.get(0));
        assertThrows(CooperException.class, () -> original.edit(3, new Note("bad")));
        assertThrows(CooperException.class, () -> original.delete(3));
    }

    @Test
    public void find_literalPhrase_returnsOriginalNumbersRegardlessOfDefaultLocale() {
        Locale previous = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr"));
            NoteList notes = new NoteList(List.of(new Note("waist size"), new Note("Watch ARRIVAL"),
                    new Note("Watch ARRIVAL again")));
            assertEquals(List.of(2, 3), notes.find("watch arrival"));
            assertEquals(List.of(), notes.find("arrival watch"));
            assertEquals(List.of(), notes.find(".*"));
        } finally {
            Locale.setDefault(previous);
        }
    }
}
