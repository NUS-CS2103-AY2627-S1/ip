package mochi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import mochi.task.Deadline;
import mochi.task.Event;
import mochi.task.Task;
import mochi.task.Todo;

public class TaskTest {

    @Test
    public void fromFileString_todo_parsesCorrectly() throws MochiException {
        Task task = Task.fromFileString("T | 0 | read book");
        assertInstanceOf(Todo.class, task);
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    public void fromFileString_todoDone_parsesCorrectly() throws MochiException {
        Task task = Task.fromFileString("T | 1 | read book");
        assertInstanceOf(Todo.class, task);
        assertEquals("[T][X] read book", task.toString());
    }

    @Test
    public void fromFileString_deadline_parsesCorrectly() throws MochiException {
        Task task = Task.fromFileString("D | 0 | return book | 2019-12-02");
        assertInstanceOf(Deadline.class, task);
        assertEquals("[D][ ] return book (by: Dec 2 2019)", task.toString());
    }

    @Test
    public void fromFileString_deadlineDone_parsesCorrectly() throws MochiException {
        Task task = Task.fromFileString("D | 1 | return book | 2019-12-02");
        assertInstanceOf(Deadline.class, task);
        assertEquals("[D][X] return book (by: Dec 2 2019)", task.toString());
    }

    @Test
    public void fromFileString_event_parsesCorrectly() throws MochiException {
        Task task = Task.fromFileString("E | 0 | meeting | 2019-10-15 | 2019-10-16");
        assertInstanceOf(Event.class, task);
        assertEquals("[E][ ] meeting (from: Oct 15 2019 to: Oct 16 2019)", task.toString());
    }

    @Test
    public void fromFileString_eventDone_parsesCorrectly() throws MochiException {
        Task task = Task.fromFileString("E | 1 | meeting | 2019-10-15 | 2019-10-16");
        assertInstanceOf(Event.class, task);
        assertEquals("[E][X] meeting (from: Oct 15 2019 to: Oct 16 2019)", task.toString());
    }

    @Test
    public void fromFileString_tooFewParts_throwsMochiException() {
        MochiException ex = assertThrows(MochiException.class,
                () -> Task.fromFileString("T | 0"));
        assertEquals("Corrupted task data.", ex.getMessage());
    }

    @Test
    public void fromFileString_invalidDoneFlag_throwsMochiException() {
        MochiException ex = assertThrows(MochiException.class,
                () -> Task.fromFileString("T | x | read book"));
        assertEquals("Corrupted task data.", ex.getMessage());
    }

    @Test
    public void fromFileString_unknownType_returnsNull() throws MochiException {
        Task task = Task.fromFileString("X | 0 | something");
        assertEquals(null, task);
    }

    @Test
    public void fromFileString_deadlineMissingDate_throwsMochiException() {
        MochiException ex = assertThrows(MochiException.class,
                () -> Task.fromFileString("D | 0 | return book"));
        assertEquals("Corrupted deadline data.", ex.getMessage());
    }

    @Test
    public void fromFileString_eventMissingDates_throwsMochiException() {
        MochiException ex = assertThrows(MochiException.class,
                () -> Task.fromFileString("E | 0 | meeting"));
        assertEquals("Corrupted event data.", ex.getMessage());
    }

    @Test
    public void fromFileString_deadlineBadDate_throwsMochiException() {
        MochiException ex = assertThrows(MochiException.class,
                () -> Task.fromFileString("D | 0 | return book | not-a-date"));
        assertEquals("Corrupted deadline data.", ex.getMessage());
    }

    @Test
    public void markAndUnmark_flipsStatus() throws MochiException {
        Task task = Task.fromFileString("T | 0 | read book");
        task.markAsDone();
        assertEquals("[T][X] read book", task.toString());
        task.markAsNotDone();
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    public void toFileString_roundTrips() throws MochiException {
        String original = "T | 0 | read book";
        Task task = Task.fromFileString(original);
        assertEquals(original, task.toFileString());
    }

    @Test
    public void toFileString_deadline_roundTrips() throws MochiException {
        String original = "D | 0 | return book | 2019-12-02";
        Task task = Task.fromFileString(original);
        assertEquals(original, task.toFileString());
    }

    @Test
    public void toFileString_event_roundTrips() throws MochiException {
        String original = "E | 0 | meeting | 2019-10-15 | 2019-10-16";
        Task task = Task.fromFileString(original);
        assertEquals(original, task.toFileString());
    }
}
