package sophon.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class TaskTest {
    @Test
    public void markAndUnmark_updatesStatusAndDisplay() {
        Todo todo = new Todo("read book");

        assertEquals(" ", todo.getStatusIcon());
        assertEquals("[T][ ] read book", todo.toString());

        todo.markAsDone();
        assertEquals("X", todo.getStatusIcon());
        assertEquals("[T][X] read book", todo.toString());

        todo.markAsNotDone();
        assertEquals(" ", todo.getStatusIcon());
    }

    @Test
    public void toFileString_allTaskTypes_returnsStorageFormat() {
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("return book", LocalDate.parse("2019-10-15"));
        Event event = new Event("meeting", LocalDate.parse("2019-10-15"), LocalDate.parse("2019-10-16"));
        deadline.markAsDone();

        assertEquals("T | 0 | read book", todo.toFileString());
        assertEquals("D | 1 | return book | 2019-10-15", deadline.toFileString());
        assertEquals("E | 0 | meeting | 2019-10-15 | 2019-10-16", event.toFileString());
    }

    @Test
    public void toString_datedTasks_returnsFriendlyDates() {
        Deadline deadline = new Deadline("return book", LocalDate.parse("2019-10-15"));
        Event event = new Event("meeting", LocalDate.parse("2019-10-15"), LocalDate.parse("2019-10-16"));

        assertEquals("[D][ ] return book (by: Oct 15 2019)", deadline.toString());
        assertEquals("[E][ ] meeting (from: Oct 15 2019 to: Oct 16 2019)", event.toString());
    }

    @Test
    public void containsKeyword_differentMatchDistances_returnsExpectedResults() {
        Task task = new Todo("go read encyclopedia");

        assertTrue(task.containsKeyword("go"));
        assertFalse(task.containsKeyword("so"));
        assertTrue(task.containsKeyword("reed"));
        assertFalse(task.containsKeyword("roadway"));
        assertTrue(task.containsKeyword("encyclopadia"));
        assertFalse(task.containsKeyword("encyclopxyz"));
    }
}
