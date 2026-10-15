package ff15.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import ff15.FF15Exception;

/** Tests how a {@link Todo} shows itself to the user and to the save file. */
public class TodoTest {

    @Test
    public void toString_newTodo_showsEmptyStatusBox() {
        assertEquals("[T][ ] read book", new Todo("read book").toString());
    }

    @Test
    public void toString_doneTodo_showsCrossInStatusBox() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    public void toString_afterMarkAsNotDone_showsEmptyStatusBoxAgain() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        todo.markAsNotDone();
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toFileFormat_newTodo_writesZeroForNotDone() {
        assertEquals("T | 0 | read book", new Todo("read book").toFileFormat());
    }

    @Test
    public void toFileFormat_doneTodo_writesOneForDone() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("T | 1 | read book", todo.toFileFormat());
    }

    @Test
    public void occursIn_anyRange_returnsFalse() throws FF15Exception {
        Todo todo = new Todo("read book");
        assertFalse(todo.occursIn(DateRange.parse("2019-12-02")));
        assertFalse(todo.occursIn(DateRange.parse("2019-12")));
        assertFalse(todo.occursIn(DateRange.parse("2019")));
    }
}
