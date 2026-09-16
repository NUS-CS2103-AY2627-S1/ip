package will.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class TaskTest {

    @Test
    public void toString_newTodo_showsTypeSymbolAndBlankStatusIcon() {
        Todo todo = new Todo("read book");
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toString_afterMarkAsDone_showsXStatusIcon() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    public void toString_afterMarkAsNotDone_revertsToBlankStatusIcon() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        todo.markAsNotDone();
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toSaveFormat_newTodo_isPipeDelimitedWithNotDoneFlag() {
        Todo todo = new Todo("read book");
        assertEquals("T | 0 | read book", todo.toSaveFormat());
    }

    @Test
    public void toSaveFormat_afterMarkAsDone_flagSwitchesToOne() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("T | 1 | read book", todo.toSaveFormat());
    }

    @Test
    public void occursOn_plainTaskWithNoDate_alwaysReturnsFalse() {
        Todo todo = new Todo("read book");
        assertFalse(todo.occursOn(LocalDate.of(2019, 10, 15)));
    }

    @Test
    public void getDescription_afterConstruction_returnsGivenDescription() {
        Todo todo = new Todo("read book");
        assertEquals("read book", todo.getDescription());
    }

    @Test
    public void getType_todo_returnsTodoType() {
        Todo todo = new Todo("read book");
        assertEquals(TaskType.TODO, todo.getType());
    }
}
