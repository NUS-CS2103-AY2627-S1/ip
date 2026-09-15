package jaku.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Verifies ordinary task status changes and their user-facing formatting. */
class TaskRenderingTest {
    /** Verifies base, todo, deadline, and event tasks retain their type and completion details. */
    @Test
    void taskTypes_formatDescriptionsAndCompletionState() {
        Task task = new Task("base task");
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("return book", LocalDate.of(2026, 9, 20));
        Event event = new Event("meeting", "14:00", "15:00");

        task.markAsDone();
        todo.markAsDone();
        deadline.markAsDone();

        assertEquals("[X] base task", task.toString());
        assertEquals("[T][X] read book", todo.toString());
        assertEquals("[D][X] return book (by: Sep 20 2026)", deadline.toString());
        assertEquals("[E][ ] meeting (from: 14:00 to: 15:00)", event.toString());

        todo.markAsNotDone();
        assertEquals("[T][ ] read book", todo.toString());
    }
}
