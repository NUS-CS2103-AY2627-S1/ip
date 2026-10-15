package eva;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import eva.task.Deadline;
import eva.task.Event;
import eva.task.Todo;

public class TaskListTest {

    @Test
    void addMarkUnmarkDelete_updatesTaskAndList() throws EvaException {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");
        tasks.add(todo);

        assertEquals(1, tasks.size());
        assertSame(todo, tasks.getTaskAt(0));
        assertSame(todo, tasks.mark(1));
        assertEquals("[T][X] read book", todo.toString());
        assertSame(todo, tasks.unmark(1));
        assertEquals("[T][ ] read book", todo.toString());
        assertSame(todo, tasks.delete(1));
        assertEquals(0, tasks.size());
    }

    @Test
    void invalidTaskNumbers_throwEvaException() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertThrows(EvaException.class, () -> tasks.mark(0));
        assertThrows(EvaException.class, () -> tasks.unmark(2));
        assertThrows(EvaException.class, () -> tasks.delete(-1));
    }

    @Test
    void find_matchesDescriptionsWithoutCase() {
        TaskList tasks = new TaskList();
        Todo report = new Todo("Submit Report");
        tasks.add(report);
        tasks.add(new Todo("read book"));

        TaskList matches = tasks.find("report");

        assertEquals(1, matches.size());
        assertSame(report, matches.getTaskAt(0));
        assertEquals(0, tasks.find("meeting").size());
    }

    @Test
    void sort_ordersDeadlinesBeforeOtherTasks() {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");
        Deadline late = new Deadline("late", "2026-09-30");
        Event event = new Event("meeting", "09:00", "10:00");
        Deadline early = new Deadline("early", "2026-09-10");
        tasks.add(todo);
        tasks.add(late);
        tasks.add(event);
        tasks.add(early);

        tasks.sort();

        assertSame(early, tasks.getTaskAt(0));
        assertSame(late, tasks.getTaskAt(1));
        assertSame(todo, tasks.getTaskAt(2));
        assertSame(event, tasks.getTaskAt(3));
    }
}
