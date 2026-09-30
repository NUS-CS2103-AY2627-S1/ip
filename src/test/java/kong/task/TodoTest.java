package kong.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;

class TodoTest {
    @Test
    void occursOn_alwaysReturnsFalse() {
        Todo todo = new Todo("read book");

        assertFalse(todo.occursOn(LocalDate.parse("2019-10-15")));
    }

    @Test
    void getSortDate_returnsEmptyOptional() {
        Todo todo = new Todo("read book");

        assertEquals(Optional.empty(), todo.getSortDate());
    }

    @Test
    void isSameTask_sameDescription_returnsTrueIgnoringCase() {
        Todo first = new Todo("read book");
        Todo second = new Todo("READ BOOK", true);
        Todo different = new Todo("write code");

        assertTrue(first.isSameTask(second));
        assertFalse(first.isSameTask(different));
        assertFalse(first.isSameTask(null));
        assertFalse(first.isSameTask(new Deadline("read book", LocalDate.parse("2019-10-15"))));
    }

    @Test
    void equalsAndHashCode_contract_validatesEquality() {
        Todo todo1 = new Todo("read book", false);
        Todo todo2 = new Todo("read book", false);
        Todo todoDone = new Todo("read book", true);
        Todo todoDiff = new Todo("other", false);

        assertEquals(todo1, todo1);
        assertEquals(todo1, todo2);
        assertEquals(todo1.hashCode(), todo2.hashCode());

        assertNotEquals(todo1, todoDone);
        assertNotEquals(todo1, todoDiff);
        assertNotEquals(todo1, null);
        assertNotEquals(todo1, "other");
    }

    @Test
    void toStringAndToFileString_validFormats() {
        Todo incomplete = new Todo("read book");
        Todo complete = new Todo("read book", true);

        assertEquals("[T][ ] read book", incomplete.toString());
        assertEquals("[T][X] read book", complete.toString());
        assertEquals("T | 0 | read book", incomplete.toFileString());
        assertEquals("T | 1 | read book", complete.toFileString());
    }
}
