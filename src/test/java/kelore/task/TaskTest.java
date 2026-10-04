package kelore.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Tests the common status, storage, date, and keyword behavior of {@link Task}. */
public class TaskTest {
    @Test
    public void constructor_nullOrBlankDescription_assertionError() {
        assertThrows(AssertionError.class, () -> new Task(null));
        assertThrows(AssertionError.class, () -> new Task("   "));
    }

    @Test
    public void status_newThenMarkedThenUnmarked_updatesIconsAndStorageStatus() {
        Task task = new Task("read a book");

        assertEquals(" ", task.getStatusIcon());
        assertEquals("[ ] read a book", task.toString());
        assertEquals("T | 0 | read a book", task.toStorageString());

        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
        assertEquals("[X] read a book", task.toString());
        assertEquals("T | 1 | read a book", task.toStorageString());

        task.markAsNotDone();
        assertEquals(" ", task.getStatusIcon());
        assertEquals("T | 0 | read a book", task.toStorageString());
    }

    @Test
    public void occursOn_anyDate_returnsFalse() {
        Task task = new Task("undated task");

        assertFalse(task.occursOn(LocalDate.of(2026, 9, 2)));
    }

    @Test
    public void containsKeyword_exactSubstringAndCase_matchesOnlyExactCase() {
        Task task = new Task("Read a storybook");

        assertTrue(task.containsKeyword("book"));
        assertTrue(task.containsKeyword("Read a"));
        assertFalse(task.containsKeyword("read"));
        assertFalse(task.containsKeyword("novel"));
    }

    @Test
    public void containsCloseKeyword_shortKeyword_allowsNoEdits() {
        Task task = new Task("go to the gym");

        assertTrue(task.containsCloseKeyword("GO"));
        assertFalse(task.containsCloseKeyword("gi"));
    }

    @Test
    public void containsCloseKeyword_mediumKeyword_allowsOneEdit() {
        Task task = new Task("return books tomorrow");

        assertTrue(task.containsCloseKeyword("boks"));
        assertTrue(task.containsCloseKeyword("book"));
        assertTrue(task.containsCloseKeyword("boooks"));
        assertFalse(task.containsCloseKeyword("back"));
    }

    @Test
    public void containsCloseKeyword_longKeyword_allowsTwoEdits() {
        Task task = new Task("complete assignment");

        assertTrue(task.containsCloseKeyword("assigment"));
        assertTrue(task.containsCloseKeyword("assignmantx"));
        assertFalse(task.containsCloseKeyword("assignxyzt"));
    }

    @Test
    public void containsCloseKeyword_multipleWhitespace_comparesIndividualWords() {
        Task task = new Task("buy\tgreen   apples");

        assertTrue(task.containsCloseKeyword("aples"));
        assertFalse(task.containsCloseKeyword("green apple"));
    }
}
