package wodan.task; // same package as the class being tested

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Todo}.
 */
public class TodoTest {
    @Test
    public void constructor_validDescription_storedAndNotDone() {
        Todo todo = new Todo("borrow book");
        assertEquals("borrow book", todo.getDescription());
        assertEquals(" ", todo.getStatusIcon());
    }

    @Test
    public void constructor_descriptionWithSpaces_storedAsGiven() {
        Todo todo = new Todo("  borrow book  ");
        assertEquals("  borrow book  ", todo.getDescription());
    }

    @Test
    public void getDescription_validTodo_returnsGivenText() {
        Todo todo = new Todo("borrow book");
        assertEquals("borrow book", todo.getDescription());
    }

    @Test
    public void getStatusIcon_unmarkedTodo_space() {
        Todo todo = new Todo("borrow book");
        assertEquals(" ", todo.getStatusIcon());
    }

    @Test
    public void getStatusIcon_markedTodo_showsX() {
        Todo todo = new Todo("borrow book");
        todo.markAsDone();
        assertEquals("X", todo.getStatusIcon());
    }

    @Test
    public void markAsDone_unmarkedTodo_becomesDone() {
        Todo todo = new Todo("borrow book");
        todo.markAsDone();
        assertEquals("X", todo.getStatusIcon());
        assertEquals("[T][X] borrow book", todo.toString());
    }

    @Test
    public void markAsDone_alreadyDone_staysDone() {
        Todo todo = new Todo("borrow book");
        todo.markAsDone();
        todo.markAsDone();
        assertEquals("X", todo.getStatusIcon());
    }

    @Test
    public void markAsUndone_markedTodo_becomesNotDone() {
        Todo todo = new Todo("borrow book");
        todo.markAsDone();
        todo.markAsUndone();
        assertEquals(" ", todo.getStatusIcon());
        assertEquals("[T][ ] borrow book", todo.toString());
    }

    @Test
    public void markAsUndone_alreadyNotDone_staysNotDone() {
        Todo todo = new Todo("borrow book");
        todo.markAsUndone();
        assertEquals(" ", todo.getStatusIcon());
    }

    @Test
    public void toString_unmarkedTodo_typeTAndEmptyStatus() {
        Todo todo = new Todo("borrow book");
        assertEquals("[T][ ] borrow book", todo.toString());
    }

    @Test
    public void toString_markedTodo_typeTAndXStatus() {
        Todo todo = new Todo("borrow book");
        todo.markAsDone();
        assertEquals("[T][X] borrow book", todo.toString());
    }

    @Test
    public void toStorageString_unmarkedTodo_zeroFlag() {
        Todo todo = new Todo("borrow book");
        assertEquals("T | 0 | borrow book | general", todo.toStorageString());
    }

    @Test
    public void toStorageString_markedTodo_oneFlag() {
        Todo todo = new Todo("borrow book");
        todo.markAsDone();
        assertEquals("T | 1 | borrow book | general", todo.toStorageString());
    }

    @Test
    public void setCategory_schoolWithMixedCase_storedLowercase() {
        Todo todo = new Todo("borrow book");
        assertEquals(Task.DEFAULT_CATEGORY, todo.getCategory());
        assertTrue(todo.isGeneral());
        todo.setCategory("School");
        assertEquals("school", todo.getCategory());
        assertFalse(todo.isGeneral());
        todo.setCategory("   ");
        assertEquals(Task.DEFAULT_CATEGORY, todo.getCategory());
        assertEquals("T | 0 | borrow book | general", todo.toStorageString());
    }

    @Test
    public void occursOn_anyDate_false() {
        Todo todo = new Todo("borrow book");
        assertFalse(todo.occursOn(LocalDate.of(2019, 12, 2)));
        assertFalse(todo.occursOn(LocalDate.of(2026, 8, 26)));
        todo.markAsDone();
        assertFalse(todo.occursOn(LocalDate.of(2019, 12, 2)));
    }

    @Test
    public void hasSameDetails_sameDescription_ignoresDoneAndNull() {
        Todo first = new Todo("borrow book");
        Todo second = new Todo("borrow book");
        second.markAsDone();
        second.setCategory("school");
        assertTrue(first.hasSameDetails(second));
        assertFalse(first.hasSameDetails(new Todo("other")));
        assertFalse(first.hasSameDetails(null));
    }

    @Test
    public void isDoneAndHasDescriptionContaining_togglesAndIgnoresCase() {
        Todo todo = new Todo("Borrow Book");
        assertFalse(todo.isDone());
        todo.markAsDone();
        assertTrue(todo.isDone());
        assertTrue(todo.hasDescriptionContaining("book"));
        assertFalse(todo.hasDescriptionContaining("2019"));
    }
}
