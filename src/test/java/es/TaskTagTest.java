package es;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class TaskTagTest {
    @Test
    void tags_addAndRemove_updatesDisplay() throws EsException {
        Todo task = new Todo("read book");
        task.addTag("#fun");
        task.addTag("#Study");
        assertEquals("[T][ ] read book #fun #Study", task.toString());
        task.removeTag("#fun");
        assertEquals("[T][ ] read book #Study", task.toString());
    }

    @Test
    void tags_rejectDuplicateAndMalformedValues() throws EsException {
        Todo task = new Todo("read book");
        task.addTag("#fun");
        assertThrows(EsException.class, () -> task.addTag("#fun"));
        assertThrows(EsException.class, () -> task.addTag("fun"));
        assertThrows(EsException.class, () -> task.addTag("#two words"));
    }
}
