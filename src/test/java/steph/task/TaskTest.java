package steph.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Task}'s shared behaviour and the display / save-file
 * formats of its subclasses {@link ToDo}, {@link Deadline} and {@link Event}.
 * The save-file formats matter most: {@code Storage} must be able to read back
 * exactly what these methods write.
 */
public class TaskTest {

    private static final LocalDateTime DATE_ONLY = LocalDateTime.of(2019, 10, 15, 0, 0);
    private static final LocalDateTime EVENING = LocalDateTime.of(2019, 10, 15, 18, 0);

    @Test
    public void newTask_isNotDoneAndKeepsName() {
        ToDo todo = new ToDo("read book");

        assertFalse(todo.isDone());
        assertEquals("read book", todo.getName());
    }

    @Test
    public void completeThenUncomplete_togglesDoneFlag() {
        ToDo todo = new ToDo("read book");

        todo.complete();
        assertTrue(todo.isDone());
        todo.uncomplete();
        assertFalse(todo.isDone());
    }

    @Test
    public void complete_calledTwice_staysDone() {
        ToDo todo = new ToDo("read book");

        todo.complete();
        todo.complete();

        assertTrue(todo.isDone());
    }

    @Test
    public void fieldSeparator_isPipe() {
        assertEquals("|", Task.FIELD_SEPARATOR);
    }

    @Test
    public void toDo_notDone_formats() {
        ToDo todo = new ToDo("read book");

        assertEquals("[T][ ] read book", todo.toString());
        assertEquals("T | 0 | read book", todo.toFileFormat());
    }

    @Test
    public void toDo_done_formats() {
        ToDo todo = new ToDo("read book");
        todo.complete();

        assertEquals("[T][X] read book", todo.toString());
        assertEquals("T | 1 | read book", todo.toFileFormat());
    }

    @Test
    public void deadline_withTime_formats() {
        Deadline deadline = new Deadline("return book", EVENING);

        // AM/PM marker case varies by JDK locale data, so compare lower-cased.
        assertEquals("[d][ ] return book (by: oct 15 2019, 6:00pm)", deadline.toString().toLowerCase());
        assertEquals("D | 0 | return book | 2019-10-15T18:00", deadline.toFileFormat());
    }

    @Test
    public void deadline_dateOnlyAndDone_formats() {
        Deadline deadline = new Deadline("return book", DATE_ONLY);
        deadline.complete();

        assertEquals("[D][X] return book (by: Oct 15 2019)", deadline.toString());
        assertEquals("D | 1 | return book | 2019-10-15T00:00", deadline.toFileFormat());
    }

    @Test
    public void event_withTimes_formats() {
        Event event = new Event("camp", DATE_ONLY, EVENING);

        assertEquals("[e][ ] camp (from: oct 15 2019 to: oct 15 2019, 6:00pm)", event.toString().toLowerCase());
        assertEquals("E | 0 | camp | 2019-10-15T00:00 | 2019-10-15T18:00", event.toFileFormat());
    }

    @Test
    public void event_done_formats() {
        Event event = new Event("camp", DATE_ONLY, DATE_ONLY.plusDays(1));
        event.complete();

        assertEquals("[E][X] camp (from: Oct 15 2019 to: Oct 16 2019)", event.toString());
        assertEquals("E | 1 | camp | 2019-10-15T00:00 | 2019-10-16T00:00", event.toFileFormat());
    }
}
