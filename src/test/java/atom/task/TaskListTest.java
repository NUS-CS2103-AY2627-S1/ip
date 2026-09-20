package atom.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import atom.exception.AtomTaskNotFoundException;

class TaskListTest {
    @Test
    void addGetRemoveAndSize_workTogether() throws AtomTaskNotFoundException {
        TaskList list = new TaskList(new ArrayList<>());
        ToDo task = new ToDo("read book");

        list.add(task);

        assertEquals(1, list.size());
        assertEquals(task, list.get(0));
        assertEquals(task, list.remove(0));
        assertEquals(0, list.size());
    }

    @Test
    void markAndUnmark_changeTaskState() throws AtomTaskNotFoundException {
        ToDo task = new ToDo("read book");
        TaskList list = new TaskList(new ArrayList<>());
        list.add(task);

        list.mark(0);
        assertEquals("[T][X] read book", task.toString());
        list.unmark(0);
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    void invalidIndex_throwsTaskNotFound() {
        TaskList list = new TaskList(new ArrayList<>());
        assertThrows(AtomTaskNotFoundException.class, () -> list.get(0));
        assertThrows(AtomTaskNotFoundException.class, () -> list.remove(-1));
    }

    @Test
    void find_returnsOnlyMatchingTasks() {
        TaskList list = new TaskList(new ArrayList<>());
        list.add(new ToDo("read book"));
        list.add(new ToDo("buy milk"));

        assertEquals("1. [T][ ] read book", list.find("book").toString());
    }

    @Test
    void schedule_returnsDeadlinesAndEventsInTimeOrder() {
        TaskList list = new TaskList(new ArrayList<>());
        list.add(new Event("meeting", "2024-10-02 14:00", "2024-10-02 16:00"));
        list.add(new Deadline("submit", "2024-10-02 09:00"));
        list.add(new ToDo("unrelated"));

        assertEquals("1. [D][ ] submit (by: Oct 2 2024, 9:00 AM)\n"
                + "2. [E][ ] meeting (from: Oct 2 2024, 2:00 PM to: Oct 2 2024, 4:00 PM)",
                list.schedule(LocalDate.of(2024, 10, 2)).toString());
    }
}
