package meowmeow.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Todo}. {@code Todo} adds no logic of its own, so this
 * really exercises the base {@link Task} behaviour it inherits unchanged:
 * the "[T]" tag, the done/not-done icon in {@link Task#toString()} and
 * {@link Task#toFileString()}, and the fact that a plain todo never matches
 * a "list &lt;date&gt;" query.
 */
public class TodoTest {

    @Test
    public void toString_notDone_showsTodoTagAndEmptyStatusBox() {
        assertEquals("[T][ ] borrow book", new Todo("borrow book").toString());
    }

    @Test
    public void toString_afterMarkingDone_showsDoneIcon() {
        Todo todo = new Todo("borrow book");
        todo.setStatus(TaskStatus.DONE);

        assertEquals("[T][X] borrow book", todo.toString());
    }

    @Test
    public void toFileString_notDone_pipeSeparatedWithZeroFlag() {
        assertEquals("T | 0 | borrow book", new Todo("borrow book").toFileString());
    }

    @Test
    public void toFileString_done_pipeSeparatedWithOneFlag() {
        Todo todo = new Todo("borrow book");
        todo.setStatus(TaskStatus.DONE);

        assertEquals("T | 1 | borrow book", todo.toFileString());
    }

    @Test
    public void newTodo_startsNotDone() {
        assertEquals(TaskStatus.NOT_DONE, new Todo("borrow book").getStatus());
    }

    @Test
    public void newTodo_startsWithNoPriority() {
        assertEquals(TaskPriority.NONE, new Todo("borrow book").getPriority());
    }

    @Test
    public void toString_priorityNone_showsNoSuffix() {
        assertEquals("[T][ ] borrow book", new Todo("borrow book").toString());
    }

    @Test
    public void toString_priorityHigh_showsPrioritySuffix() {
        Todo todo = new Todo("borrow book");
        todo.setPriority(TaskPriority.HIGH);

        assertEquals("[T][ ] borrow book (priority: HIGH)", todo.toString());
    }

    @Test
    public void toFileString_priorityNone_omitsPriorityField() {
        assertEquals("T | 0 | borrow book", new Todo("borrow book").toFileString());
    }

    @Test
    public void toFileString_priorityHigh_appendsPriorityField() {
        Todo todo = new Todo("borrow book");
        todo.setPriority(TaskPriority.HIGH);

        assertEquals("T | 0 | borrow book | HIGH", todo.toFileString());
    }

    @Test
    public void occursOn_anyDate_false() {
        // A todo has no date, so it is never part of a "list <date>" result.
        assertFalse(new Todo("borrow book").occursOn(LocalDate.of(2019, 12, 2)));
    }
}
