package bob.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import bob.exception.BobException;

/**
 * Tests the behavior of {@link ToDo}.
 */
public class ToDoTest {

    @Test
    public void toString_unmarkedAndMarked_formattedCorrectly() {
        ToDo todo = new ToDo("buy milk");
        assertEquals("[T][ ] buy milk", todo.toString());

        todo.mark();
        assertEquals("[T][X] buy milk", todo.toString());

        todo.unmark();
        assertEquals("[T][ ] buy milk", todo.toString());
    }

    @Test
    public void export_unmarkedAndMarked_formattedCorrectly() {
        ToDo todo = new ToDo("buy milk");
        assertEquals("T\u00010\u0001buy milk", todo.export());

        todo.mark();
        assertEquals("T\u00011\u0001buy milk", todo.export());

        todo.unmark();
        assertEquals("T\u00010\u0001buy milk", todo.export());
    }

    @Test
    public void update_validName_updatesNameAndPreservesDoneStatus() throws BobException {
        ToDo todo = new ToDo("buy milk");
        todo.mark();

        Task updated = todo.update("buy almond milk", null, null, null);
        assertEquals("[T][X] buy almond milk", updated.toString());

        Task unchanged = todo.update(null, null, null, null);
        assertEquals("[T][X] buy milk", unchanged.toString());
    }

    @Test
    public void update_incompatibleDateFlags_throwsBobException() {
        ToDo todo = new ToDo("buy milk");
        LocalDateTime date = LocalDateTime.of(2026, 12, 12, 12, 0);

        assertThrows(BobException.class, () -> todo.update(null, date, null, null));
        assertThrows(BobException.class, () -> todo.update(null, null, date, null));
        assertThrows(BobException.class, () -> todo.update(null, null, null, date));
    }
}
