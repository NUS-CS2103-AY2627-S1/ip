package elora.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class TodoTest {

    @Test
    void toString_notDoneTodo_showsTypeAndBlankBracket() {
        Todo todo = new Todo("read book");
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    void toString_doneTodo_showsTypeAndXBracket() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    void toSaveFormat_notDoneTodo_producesPipeDelimitedLine() {
        Todo todo = new Todo("read book");
        assertEquals("T | 0 | read book", todo.toSaveFormat());
    }

    @Test
    void toSaveFormat_doneTodo_recordsOneFlag() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("T | 1 | read book", todo.toSaveFormat());
    }

    @Test
    void isOccurringOn_anyDate_returnsFalse() {
        Todo todo = new Todo("read book");
        assertFalse(todo.isOccurringOn(LocalDate.parse("2019-10-15")));
    }

    @Test
    void getSortDate_todo_returnsNull() {
        Todo todo = new Todo("read book");
        assertNull(todo.getSortDate());
    }

    @Test
    void equals_sameDescription_returnsTrue() {
        assertEquals(new Todo("read book"), new Todo("read book"));
    }

    @Test
    void equals_differentDescription_returnsFalse() {
        assertNotEquals(new Todo("read book"), new Todo("return book"));
    }

    @Test
    void equals_differentDoneStatus_stillEqual() {
        Todo done = new Todo("read book");
        done.markAsDone();
        assertEquals(done, new Todo("read book"));
    }

    @Test
    void equals_differentTaskType_returnsFalse() {
        assertNotEquals(new Todo("read book"), new Task("read book"));
    }
}
