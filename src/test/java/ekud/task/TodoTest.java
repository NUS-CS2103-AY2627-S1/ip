package ekud.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Todo} display text, save text, and date matching.
 */
public class TodoTest {
    @Test
    public void toString_incomplete_showsEmptyStatusBox() {
        Todo todo = new Todo("read book");
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toString_completed_showsXInStatusBox() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    public void markAsNotDone_afterMarkingDone_clearsStatus() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        todo.markAsNotDone();
        assertFalse(todo.isDone());
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toCommandString_returnsTodoCommand() {
        assertEquals("todo read book", new Todo("read book").toCommandString());
    }

    @Test
    public void occursOn_anyDate_returnsFalse() {
        Todo todo = new Todo("read book");
        assertFalse(todo.occursOn(LocalDate.of(2019, 12, 2)));
    }

    @Test
    public void descriptionContains_ignoresCaseAndMatchesSubstring() {
        Todo todo = new Todo("read book");
        assertTrue(todo.descriptionContains("book"));
        assertTrue(todo.descriptionContains("BOOK"));
        assertTrue(todo.descriptionContains("read"));
        assertFalse(todo.descriptionContains("notebook"));
    }

    @Test
    public void getTypeIcon_returnsT() {
        assertEquals("T", new Todo("read book").getTypeIcon());
    }

    @Test
    public void getStatusIcon_reflectsDoneFlag() {
        Todo todo = new Todo("read book");
        assertEquals(" ", todo.getStatusIcon());
        todo.markAsDone();
        assertEquals("X", todo.getStatusIcon());
    }
}
