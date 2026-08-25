package matthew.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import matthew.exception.MatthewException;

/** Tests task state, formatting, dates, and task-list operations. */
class TaskTest {
    @Test
    void taskMarkingAndFormatting() {
        Task task = new Task("read book");

        assertFalse(task.isDone());
        assertEquals("read book", task.getDescription());
        assertEquals(" ", task.getStatusIcon());
        assertEquals("[ ] read book", task.toString());

        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
        assertEquals("[X] read book", task.toString());

        task.markAsNotDone();
        assertFalse(task.isDone());
    }

    @Test
    void specialisedTasksExposeDatesAndFormatThemselves() {
        LocalDateTime start = LocalDateTime.of(2026, 8, 22, 9, 30);
        LocalDateTime end = LocalDateTime.of(2026, 8, 24, 17, 0);
        Deadline deadline = new Deadline("submit work", start);
        Event event = new Event("camp", start, end);

        assertEquals("[T][ ] buy milk", new Todo("buy milk").toString());
        assertEquals(start, deadline.getBy());
        assertTrue(deadline.occursOn(start.toLocalDate()));
        assertFalse(deadline.occursOn(start.toLocalDate().plusDays(1)));
        assertTrue(deadline.toString().matches(
                "\\[D]\\[ ] submit work \\(by: .+22 2026, 9:30 .+\\)"));

        assertEquals(start, event.getFrom());
        assertEquals(end, event.getTo());
        assertTrue(event.occursOn(LocalDate.of(2026, 8, 23)));
        assertFalse(event.occursOn(LocalDate.of(2026, 8, 25)));
        assertTrue(event.toString().matches(
                "\\[E]\\[ ] camp \\(from: .+22 2026, 9:30 .+ to: .+24 2026, 5:00 .+\\)"));
    }

    @Test
    void taskListManagesTasksUsingOneBasedNumbers() throws MatthewException {
        Task first = new Todo("first");
        Task second = new Deadline("second", LocalDateTime.of(2026, 8, 22, 12, 0));
        Task third = new Event("third", LocalDateTime.of(2026, 8, 22, 9, 0),
                LocalDateTime.of(2026, 8, 23, 9, 0));
        TaskList tasks = new TaskList(new ArrayList<>());

        tasks.add(first);
        tasks.add(second);
        tasks.add(third);
        assertEquals(3, tasks.size());
        assertEquals(second, tasks.get(2));
        assertEquals(first, tasks.mark(1));
        assertTrue(first.isDone());
        assertEquals(first, tasks.unmark(1));
        assertFalse(first.isDone());
        assertEquals(1, tasks.getTasksContaining("SECOND").size());
        assertTrue(tasks.getTasksContaining("second").contains(second));
        assertEquals(second, tasks.delete(2));
        assertEquals(2, tasks.size());

        ArrayList<Task> copy = tasks.getAll();
        copy.clear();
        assertEquals(2, tasks.size());
        assertEquals(1, tasks.getTasksOn(LocalDate.of(2026, 8, 22)).size());
        assertTrue(tasks.getTasksOn(LocalDate.of(2026, 8, 24)).isEmpty());
    }

    @Test
    void taskListRejectsInvalidTaskNumbers() {
        TaskList tasks = new TaskList();

        MatthewException zero = assertThrows(MatthewException.class, () -> tasks.get(0));
        MatthewException missing = assertThrows(MatthewException.class, () -> tasks.delete(1));

        assertEquals("There is no task numbered 0.", zero.getMessage());
        assertEquals("There is no task numbered 1.", missing.getMessage());
    }
}
