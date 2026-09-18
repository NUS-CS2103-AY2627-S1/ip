package alfred.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link ToDo}, and through it the {@link Task} behavior every kind
 * inherits: marking, keyword matching, and falling on no day.
 */
public class ToDoTest {
    @Test
    public void occursOn_anyDay_false() {
        ToDo todo = new ToDo("read book");
        assertFalse(todo.occursOn(LocalDate.of(2019, 10, 15)));
        assertFalse(todo.occursOn(LocalDate.of(1999, 1, 1)));
    }

    @Test
    public void matches_wordInDescription_true() {
        assertTrue(new ToDo("read book").matches("book"));
    }

    @Test
    public void matches_wordNotInDescription_false() {
        assertFalse(new ToDo("read book").matches("meeting"));
    }

    @Test
    public void matches_differentCase_true() {
        assertTrue(new ToDo("read book").matches("BOOK"));
        assertTrue(new ToDo("Read Book").matches("book"));
    }

    @Test
    public void matches_partOfLongerWord_true() {
        assertTrue(new ToDo("visit bookshop").matches("book"));
    }

    @Test
    public void matches_phraseSpanningWords_true() {
        assertTrue(new ToDo("read book tonight").matches("read book"));
    }

    @Test
    public void matches_wordsPresentButNotAdjacent_false() {
        assertFalse(new ToDo("read a book").matches("read book"));
    }

    @Test
    public void isSameTask_sameDescriptionDifferentCase_true() {
        assertTrue(new ToDo("read book").isSameTask(new ToDo("Read Book")));
    }

    @Test
    public void isSameTask_deadlineWithSameDescription_false() {
        assertFalse(new ToDo("read book").isSameTask(new Deadline("read book", LocalDate.of(2019, 10, 15))));
    }

    @Test
    public void toString_newTask_statusBoxEmpty() {
        assertEquals("[T][ ] read book", new ToDo("read book").toString());
    }

    @Test
    public void markDone_newTask_statusBoxMarked() {
        ToDo todo = new ToDo("read book");
        todo.markDone();
        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    public void unmarkDone_doneTask_statusBoxEmptyAgain() {
        ToDo todo = new ToDo("read book");
        todo.markDone();
        todo.unmarkDone();
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toFileFields_notDone_typeStatusAndDescription() {
        assertEquals(List.of("T", "0", "read book"), new ToDo("read book").toFileFields());
    }

    @Test
    public void toFileFields_anyTask_fieldsCannotBeChanged() {
        List<String> fields = new ToDo("read book").toFileFields();

        assertThrows(UnsupportedOperationException.class, () -> fields.add("extra"));
    }

    @Test
    public void toFileFields_done_statusIsOne() {
        ToDo todo = new ToDo("read book");
        todo.markDone();
        assertEquals(List.of("T", "1", "read book"), todo.toFileFields());
    }

    @Test
    public void toString_prioritySet_boxBetweenStatusAndDescription() {
        ToDo todo = new ToDo("read book");
        todo.setPriority(Priority.HIGH);
        assertEquals("[T][ ][HIGH] read book", todo.toString());
    }

    @Test
    public void toFileFields_prioritySet_levelAfterDescription() {
        ToDo todo = new ToDo("read book");
        todo.setPriority(Priority.MEDIUM);
        assertEquals(List.of("T", "0", "read book", "MEDIUM"), todo.toFileFields());
    }
}
