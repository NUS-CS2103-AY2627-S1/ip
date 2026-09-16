package serangooner.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;

public class TodoTest {
    @Test
    public void toSaveFormat_notDone_writesTypeCodeFlagAndDescription() {
        assertEquals("T | 0 | read book", new Todo("read book").toSaveFormat());
    }

    @Test
    public void toSaveFormat_done_writesTheDoneFlag() {
        Task todo = new Todo("read book");
        todo.markDone();
        assertEquals("T | 1 | read book", todo.toSaveFormat());
    }

    @Test
    public void toString_notDone_showsAnEmptyBox() {
        assertEquals("[T][ ] read book", new Todo("read book").toString());
    }

    @Test
    public void toString_done_showsATickedBox() {
        Task todo = new Todo("read book");
        todo.markDone();
        assertEquals("[T][✓] read book", todo.toString());
    }

    @Test
    public void isWithin_anyRange_returnsFalseBecauseATodoHasNoDate() {
        assertFalse(new Todo("read book")
                .isWithin(LocalDate.of(2000, 1, 1), LocalDate.of(2100, 1, 1)));
    }

    @Test
    public void parseSaveFields_validLine_returnsTodo() {
        Optional<Task> task = Todo.parseSaveFields(new String[] {"T", "0", "read book"});
        assertTrue(task.isPresent());
        assertEquals("[T][ ] read book", task.get().toString());
    }

    @Test
    public void parseSaveFields_doneFlag_returnsTaskAlreadyMarkedDone() {
        Optional<Task> task = Todo.parseSaveFields(new String[] {"T", "1", "read book"});
        assertTrue(task.isPresent());
        assertTrue(task.get().isDone());
    }

    @Test
    public void parseSaveFields_tooFewFields_returnsNothing() {
        assertTrue(Todo.parseSaveFields(new String[] {"T", "0"}).isEmpty());
    }

    @Test
    public void parseSaveFields_tooManyFields_returnsNothing() {
        assertTrue(Todo.parseSaveFields(new String[] {"T", "0", "read book", "extra"}).isEmpty());
    }

    @Test
    public void parseSaveFields_blankField_returnsNothing() {
        assertTrue(Todo.parseSaveFields(new String[] {"T", "0", "   "}).isEmpty());
    }

    @Test
    public void parseSaveFields_unrecognizedDoneFlag_returnsNothing() {
        assertTrue(Todo.parseSaveFields(new String[] {"T", "7", "read book"}).isEmpty());
    }

    @Test
    public void getTypeName_always_namesTheKindForMessages() {
        assertEquals("todo", new Todo("read book").getTypeName());
    }
}
