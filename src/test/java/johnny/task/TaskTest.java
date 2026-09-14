package johnny.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class TaskTest {

    // --- Todo ---

    @Test
    public void todoToString_newTask_showsUndone() {
        Todo todo = new Todo("read book");
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void todoToString_markedDone_showsDone() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("[T][X] read book", todo.toString());
    }

    @Test
    public void todoToString_markedDoneThenUndone_showsUndone() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        todo.markAsNotDone();
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void todoToFileString_newTask_correctFormat() {
        Todo todo = new Todo("read book");
        assertEquals("T | 0 | read book", todo.toFileString());
    }

    @Test
    public void todoToFileString_markedDone_correctFormat() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        assertEquals("T | 1 | read book", todo.toFileString());
    }

    // --- Deadline ---

    @Test
    public void deadlineToString_newTask_showsFormattedDate() {
        Deadline deadline = new Deadline("homework", LocalDate.of(2024, 12, 2));
        assertEquals("[D][ ] homework (by: Dec 02 2024)", deadline.toString());
    }

    @Test
    public void deadlineToString_markedDone_showsDone() {
        Deadline deadline = new Deadline("homework", LocalDate.of(2024, 12, 2));
        deadline.markAsDone();
        assertEquals("[D][X] homework (by: Dec 02 2024)", deadline.toString());
    }

    @Test
    public void deadlineToFileString_newTask_usesIsoDate() {
        Deadline deadline = new Deadline("homework", LocalDate.of(2024, 12, 2));
        assertEquals("D | 0 | homework | 2024-12-02", deadline.toFileString());
    }

    @Test
    public void deadlineToFileString_markedDone_correctFormat() {
        Deadline deadline = new Deadline("homework", LocalDate.of(2024, 12, 2));
        deadline.markAsDone();
        assertEquals("D | 1 | homework | 2024-12-02", deadline.toFileString());
    }

    // --- Event ---

    @Test
    public void eventToString_newTask_showsFormattedDates() {
        Event event = new Event("meeting",
                LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 3));
        assertEquals("[E][ ] meeting (from: Mar 01 2024 to: Mar 03 2024)",
                event.toString());
    }

    @Test
    public void eventToString_markedDone_showsDone() {
        Event event = new Event("meeting",
                LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 3));
        event.markAsDone();
        assertEquals("[E][X] meeting (from: Mar 01 2024 to: Mar 03 2024)",
                event.toString());
    }

    @Test
    public void eventToFileString_newTask_usesIsoDates() {
        Event event = new Event("meeting",
                LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 3));
        assertEquals("E | 0 | meeting | 2024-03-01 | 2024-03-03",
                event.toFileString());
    }

    @Test
    public void eventToFileString_markedDone_correctFormat() {
        Event event = new Event("meeting",
                LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 3));
        event.markAsDone();
        assertEquals("E | 1 | meeting | 2024-03-01 | 2024-03-03",
                event.toFileString());
    }

    @Test
    public void eventConstructor_sameStartAndEnd_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new Event("standup",
                LocalDate.of(2024, 6, 15), LocalDate.of(2024, 6, 15)));
    }

    @Test
    public void eventConstructor_startAfterEnd_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new Event("conference",
                LocalDate.of(2024, 6, 16), LocalDate.of(2024, 6, 15)));
    }
}
