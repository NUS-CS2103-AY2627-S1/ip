package yachiyo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests the storage and display formats of {@link ToDo} tasks.
 */
public class ToDoTest {
    private final ToDo toDo = new ToDo("Read book");

    @Test
    public void toFileFormat_incompleteToDo_correctFormatReturned() {
        assertEquals("TODO | 0 | Read book", toDo.toFileFormat());
    }

    @Test
    public void toFileFormat_completedToDo_correctFormatReturned() {
        toDo.markAsDone();

        assertEquals("TODO | 1 | Read book", toDo.toFileFormat());
    }

    @Test
    public void toString_incompleteToDo_correctDisplayReturned() {
        assertEquals("[T][ ] Read book", toDo.toString());
    }

    @Test
    public void toString_completedToDo_correctDisplayReturned() {
        toDo.markAsDone();

        assertEquals("[T][X] Read book", toDo.toString());
    }

    @Test
    public void withDescription_completedToDo_descriptionChangedAndStatusPreserved() {
        toDo.markAsDone();

        ToDo editedToDo = toDo.withDescription("Return book");

        assertNotSame(toDo, editedToDo);
        assertEquals("Return book", editedToDo.getDescription());
        assertTrue(editedToDo.isCompleted());
    }
}
