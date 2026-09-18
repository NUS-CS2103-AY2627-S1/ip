package habi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests task representations, persistent formats, and completion status. */
public class TaskTest {
    @Test
    public void taskRepresentations_newTasks_matchExpectedFormat() {
        assertEquals("[T][ ] read", new Todo("read").toString());
        assertEquals("[D][ ] return (by: Sep 20 2026)",
                new Deadline("return", "2026-09-20").toString());
        assertEquals("[E][ ] meeting (from: 09:00 to: 10:00)",
                new Event("meeting", "09:00", "10:00").toString());
    }

    @Test
    public void taskDataStrings_newTasks_matchStorageFormat() {
        assertEquals("T\t0\tread", new Todo("read").toDataString());
        assertEquals("D\t0\treturn\t2026-09-20",
                new Deadline("return", "2026-09-20").toDataString());
        assertEquals("E\t0\tmeeting\t09:00\t10:00",
                new Event("meeting", "09:00", "10:00").toDataString());
        assertEquals("N\tbuy milk", new Note("buy milk").toDataString());
    }

    @Test
    public void markAndUnmark_updatesStatusAndPersistentData() {
        Task task = new Todo("read");

        task.markAsDone();

        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
        assertEquals("T\t1\tread", task.toDataString());

        task.markAsNotDone();

        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
        assertEquals("T\t0\tread", task.toDataString());
    }
}
