package baemax;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import org.junit.jupiter.api.Test;

/** Tests for {@link Task} and its subtypes: display, save format, and copying. */
public class TaskTest {

    // ---- Task itself ----

    @Test
    public void newTask_startsNotDone() {
        Task task = new Task("read book");
        assertEquals(" ", task.getStatusIcon());
        assertEquals("[ ] read book", task.toString());
        assertEquals("0 | read book", task.toFileFormat());
    }

    @Test
    public void markAsDone_thenMarkAsUndone_toggleTwice() {
        Task task = new Task("read book");
        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
        assertEquals("[X] read book", task.toString());
        task.markAsUndone();
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void getDescription_returnsTheTextItWasCreatedWith() {
        assertEquals("read book", new Task("read book").getDescription());
    }

    @Test
    public void copy_isIndependentOfTheOriginal() {
        Task original = new Task("read book");
        Task copy = original.copy();

        original.markAsDone();

        assertNotSame(original, copy);
        assertEquals("[ ] read book", copy.toString());
        assertEquals("[X] read book", original.toString());
    }

    // ---- Todo ----

    @Test
    public void todo_displayAndFileFormat() {
        Todo todo = new Todo("read book");
        assertEquals("[T][ ] read book", todo.toString());
        assertEquals("T | 0 | read book", todo.toFileFormat());
    }

    @Test
    public void todo_copy_isStillATodoAndIndependent() {
        Todo original = new Todo("read book");
        original.markAsDone();
        Task copy = original.copy();
        original.markAsUndone();

        assertInstanceOf(Todo.class, copy);
        assertEquals("[T][X] read book", copy.toString());
        assertEquals("[T][ ] read book", original.toString());
    }

    // ---- Deadline ----

    @Test
    public void deadline_displayAndFileFormat() throws BaemaxException {
        Deadline deadline = new Deadline("return book", TaskDate.parse("2019-10-15"));
        assertEquals("[D][ ] return book (by: Oct 15 2019)", deadline.toString());
        assertEquals("D | 0 | return book | 2019-10-15", deadline.toFileFormat());
    }

    @Test
    public void deadline_copy_isStillADeadlineWithTheSameDueDate() throws BaemaxException {
        Deadline original = new Deadline("return book", TaskDate.parse("2019-10-15"));
        Task copy = original.copy();

        assertInstanceOf(Deadline.class, copy);
        assertEquals("[D][ ] return book (by: Oct 15 2019)", copy.toString());
    }

    // ---- Event ----

    @Test
    public void event_displayAndFileFormat() throws BaemaxException {
        Event event = new Event("camp", TaskDate.parse("2019-08-05"), TaskDate.parse("2019-08-07"));
        assertEquals("[E][ ] camp (from: Aug 05 2019 to: Aug 07 2019)", event.toString());
        assertEquals("E | 0 | camp | 2019-08-05 | 2019-08-07", event.toFileFormat());
    }

    @Test
    public void event_copy_isStillAnEventWithTheSameDateRange() throws BaemaxException {
        Event original = new Event("camp", TaskDate.parse("2019-08-05"), TaskDate.parse("2019-08-07"));
        Task copy = original.copy();

        assertInstanceOf(Event.class, copy);
        assertEquals("[E][ ] camp (from: Aug 05 2019 to: Aug 07 2019)", copy.toString());
    }
}
