package benbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests note parsing and formatting. */
class NoteTest {
    @Test
    void createFromCommand_validText_createsNote() throws InvalidCommandException {
        Note note = Note.createFromCommand(new String[] {"note", "Watch", "Arrival"});

        assertEquals("[N] Watch Arrival", note.toString());
        assertEquals("note Watch Arrival", note.toStorageString());
    }

    @Test
    void createFromCommand_missingText_throwsException() {
        InvalidCommandException exception = assertThrows(InvalidCommandException.class, () ->
                Note.createFromCommand(new String[] {"note"}));

        assertEquals("Use: " + Note.USAGE, exception.getMessage());
    }

    @Test
    void note_nullOrBlankText_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Note(null));
        assertThrows(AssertionError.class, () -> new Note(" "));
    }
}
