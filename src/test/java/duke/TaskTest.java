package duke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TaskTest {

    // ---- Task base behaviour ----

    @Test
    public void markDone_setsStatusIcon() {
        Task task = new Todo("read book");
        task.markDone();
        assertTrue(task.isDone());
        assertEquals("[T][X] read book", task.toString());
    }

    @Test
    public void unmarkDone_clearsStatusIcon() {
        Task task = new Todo("read book");
        task.markDone();
        task.unmarkDone();
        assertFalse(task.isDone());
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    public void toSaveFormat_todoNotDone() {
        assertEquals("T | 0 | read book", new Todo("read book").toSaveFormat());
    }

    @Test
    public void toSaveFormat_deadlineDone() throws DuckeException {
        Task deadline = Deadline.of("return book /by 2019-10-15");
        deadline.markDone();
        assertEquals("D | 1 | return book | 2019-10-15", deadline.toSaveFormat());
    }

    @Test
    public void toSaveFormat_event() throws DuckeException {
        Task event = Event.of("meeting /from 2019-10-01 /to 2019-10-02");
        assertEquals("E | 0 | meeting | 2019-10-01 | 2019-10-02", event.toSaveFormat());
    }

    // ---- Todo ----

    @Test
    public void todoOf_validInput_createsTodo() throws DuckeException {
        assertEquals("[T][ ] read book", Todo.of("read book").toString());
    }

    @Test
    public void todoOf_emptyInput_exceptionThrown() {
        assertThrows(DuckeException.class, () -> Todo.of(""));
    }

    @Test
    public void todoOf_blankInput_exceptionThrown() {
        assertThrows(DuckeException.class, () -> Todo.of("   "));
    }

    // ---- Deadline ----

    @Test
    public void deadlineOf_validInput_createsDeadline() throws DuckeException {
        assertEquals("[D][ ] return book (by: Oct 15 2019)",
                Deadline.of("return book /by 2019-10-15").toString());
    }

    @Test
    public void deadlineOf_missingByMarker_exceptionThrown() {
        assertThrows(DuckeException.class, () -> Deadline.of("return book"));
    }

    @Test
    public void deadlineOf_emptyDescription_exceptionThrown() {
        assertThrows(DuckeException.class, () -> Deadline.of("/by 2019-10-15"));
    }

    @Test
    public void deadlineOf_blankByTime_exceptionThrown() {
        assertThrows(DuckeException.class, () -> Deadline.of("return book /by "));
    }

    @Test
    public void deadlineOf_invalidDateFormat_exceptionThrown() {
        assertThrows(DuckeException.class, () -> Deadline.of("return book /by Sunday"));
    }

    // ---- Event ----

    @Test
    public void eventOf_validInput_createsEvent() throws DuckeException {
        assertEquals("[E][ ] meeting (from: Oct 1 2019 to: Oct 2 2019)",
                Event.of("meeting /from 2019-10-01 /to 2019-10-02").toString());
    }

    @Test
    public void eventOf_missingToMarker_exceptionThrown() {
        assertThrows(DuckeException.class, () -> Event.of("meeting /from 2019-10-01"));
    }

    @Test
    public void eventOf_missingFromMarker_exceptionThrown() {
        assertThrows(DuckeException.class, () -> Event.of("meeting /to 2019-10-02"));
    }

    @Test
    public void eventOf_missingBothMarkers_exceptionThrown() {
        assertThrows(DuckeException.class, () -> Event.of("meeting"));
    }

    @Test
    public void eventOf_emptyDescription_exceptionThrown() {
        assertThrows(DuckeException.class, () -> Event.of("/from 2019-10-01 /to 2019-10-02"));
    }

    @Test
    public void eventOf_blankToTime_exceptionThrown() {
        assertThrows(DuckeException.class, () -> Event.of("meeting /from 2019-10-01 /to "));
    }

    @Test
    public void eventOf_invalidDateFormat_exceptionThrown() {
        assertThrows(DuckeException.class, () -> Event.of("meeting /from Monday /to Tuesday"));
    }
}
