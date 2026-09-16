package bambolino;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import bambolino.task.Deadline;
import bambolino.task.Event;
import bambolino.task.Task;
import bambolino.task.TaskList;
import bambolino.task.Todo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Checks task display, state changes, searching, and list ownership. */
class TaskTest {
    @Test
    void task_statusAndSearch_preserveDescription() {
        Task task = new Task("Read BOOK");
        assertEquals("Read BOOK", task.getDescription());
        assertEquals("[ ] Read BOOK", task.toString());
        assertTrue(task.containsKeyword("book"));
        assertFalse(task.containsKeyword("meeting"));
        task.markAsDone();
        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
        task.unmarkAsDone();
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    void task_types_displayDetailsAndCompletion() {
        Todo todo = new Todo("book");
        Deadline deadline = new Deadline("book", LocalDate.of(2024, 2, 29));
        Event event = new Event("meeting", "Mon 2pm", "4pm");
        todo.markAsDone();
        deadline.markAsDone();
        event.markAsDone();
        assertEquals("[T][X] book", todo.toString());
        assertEquals("[D][X] book (by: Feb 29 2024)", deadline.toString());
        assertEquals("[E][X] meeting (from: Mon 2pm to: 4pm)", event.toString());
        assertEquals("T|1|Ym9vaw==", todo.toStorageString());
        assertEquals("D|1|Ym9vaw==|MjAyNC0wMi0yOQ==", deadline.toStorageString());
    }

    @Test
    void taskList_mutationAndSnapshots_preserveOrderAndOwnership() {
        Task first = new Todo("first");
        List<Task> original = new ArrayList<>(List.of(first));
        TaskList tasks = new TaskList(original);
        original.clear();
        assertSame(first, tasks.get(0));
        List<Task> snapshot = tasks.asList();
        Task last = new Todo("last");
        tasks.add(last);
        assertSame(last, tasks.getLast());
        assertEquals(1, snapshot.size());
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(last));
        assertSame(first, tasks.remove(0));
        assertSame(last, tasks.get(0));
        tasks.remove(0);
        assertTrue(tasks.isEmpty());
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.remove(0));
        assertTrue(new TaskList().isEmpty());
    }
}
