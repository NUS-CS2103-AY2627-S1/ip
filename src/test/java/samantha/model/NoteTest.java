package samantha.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import samantha.exception.TaskValidationException;

class NoteTest {
    @Test
    void constructor_blankContent_taskValidationExceptionThrown() {
        assertThrows(TaskValidationException.class, () -> new Note("   "));
    }

    @Test
    void edit_validContent_updatesContentAndSerialization() throws TaskValidationException {
        Note note = new Note("Remember the movie title");

        note.edit("Remember to watch Inception");

        assertEquals("Remember to watch Inception", note.getContent());
        assertEquals("Remember to watch Inception", note.toString());
        assertEquals("Remember to watch Inception", note.toFileString());
    }

    @Test
    void edit_blankContent_keepsOriginalContent() throws TaskValidationException {
        Note note = new Note("Original content");

        assertThrows(TaskValidationException.class, () -> note.edit(" "));

        assertEquals("Original content", note.getContent());
    }
}
