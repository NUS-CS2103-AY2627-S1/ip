package bro.task;

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
    public void toString_uncompletedTodo_formattedStringReturned() {
        assertEquals("[T][ ] test todo task", new Todo("test todo task").toString());
    }

    @Test
    public void toFileFormat_uncompletedTodo_formattedStringReturned() {
        assertEquals("T | 0 | test todo task", new Todo("test todo task").toFileFormat());
    }

    @Test
    public void markDone_uncompletedTodo_taskMarkedDone() {
        Todo todo = new Todo("test todo task");
        todo.markDone();
        assertTrue(todo.isDone());
        assertEquals("[T][X] test todo task", todo.toString());
        assertEquals("T | 1 | test todo task", todo.toFileFormat());
    }

    @Test
    public void unmarkDone_completedTodo_taskUnmarkedDone() {
        Todo todo = new Todo("test todo task");
        // mark as done first
        todo.markDone();
        assertTrue(todo.isDone());
        assertEquals("[T][X] test todo task", todo.toString());
        assertEquals("T | 1 | test todo task", todo.toFileFormat());

        // unmark as done
        todo.unmarkDone();
        assertFalse(todo.isDone());
        assertEquals("[T][ ] test todo task", todo.toString());
        assertEquals("T | 0 | test todo task", todo.toFileFormat());
    }

    @Test
    public void isOnDate_completedAndUncompletedTodo_correctBooleanReturned() {
        Todo todo = new Todo("test todo task");
        // todo is not done, so it should always return true
        LocalDate testDate = LocalDate.of(2026, 8, 28);
        assertTrue(todo.isOnDate(testDate));

        // mark as done, then it should return false
        todo.markDone();
        assertFalse(todo.isOnDate(testDate));
    }

    @Test
    public void containsKeywords_variousCases_correctBooleanReturned() {
        Todo todo = new Todo("Read Book for CS2103T");
        assertTrue(todo.containsKeywords("read"));
        assertTrue(todo.containsKeywords("READ"));
        assertTrue(todo.containsKeywords("BOOK"));
        assertTrue(todo.containsKeywords("bOoK"));
        assertTrue(todo.containsKeywords("cs2103t"));
        assertTrue(todo.containsKeywords("CS2103T"));
        assertTrue(todo.containsKeywords("Read Book"));
        assertTrue(todo.containsKeywords("rEaD bOoK"));
        assertFalse(todo.containsKeywords("math"));
    }

    @Test
    public void setDescription_validDescription_descriptionUpdated() {
        Todo todo = new Todo("initial description");
        todo.setDescription("updated description");
        assertEquals("updated description", todo.getDescription());
        assertEquals("updated description", todo.getTask());
        assertEquals("[T][ ] updated description", todo.toString());
        assertEquals("T | 0 | updated description", todo.toFileFormat());
    }

    @Test
    public void equalsAndHashCode_matchingAndDifferent_behaveCorrectly() {
        Todo t1 = new Todo("buy groceries");
        Todo t2 = new Todo("BUY GROCERIES");
        Todo t3 = new Todo("read book");

        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());
        assertFalse(t1.equals(t3));
        assertFalse(t1.equals(null));
        assertFalse(t1.equals("buy groceries"));
    }

    @Test
    public void setDescription_completedTodo_preservesDoneStatus() {
        Todo todo = new Todo("initial description");
        todo.markDone();
        todo.setDescription("updated description");
        assertTrue(todo.isDone());
        assertEquals("[T][X] updated description", todo.toString());
        assertEquals("T | 1 | updated description", todo.toFileFormat());
    }
}
