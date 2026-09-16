package discitrack.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

public class TaskListTest {
    @Test
    public void findTasksByDate_matchingDeadlineDate_returnsDeadline() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("submit homework", LocalDate.parse("2026-08-29")));

        List<Task> tasksOnDate = tasks.findTasksByDate(LocalDate.parse("2026-08-29"));

        assertEquals(1, tasksOnDate.size());
        assertInstanceOf(Deadline.class, tasksOnDate.get(0));
        assertEquals("submit homework", tasksOnDate.get(0).getActivity());
    }

    @Test
    public void findTasksByDate_matchingEventEndDate_returnsEvent() {
        TaskList tasks = new TaskList();
        tasks.add(new Event("camp", LocalDate.parse("2026-09-01"), LocalDate.parse("2026-09-03")));
        tasks.add(new Deadline("submit homework", LocalDate.parse("2026-08-29")));

        List<Task> tasksOnDate = tasks.findTasksByDate(LocalDate.parse("2026-09-03"));

        assertEquals(1, tasksOnDate.size());
        assertInstanceOf(Event.class, tasksOnDate.get(0));
        assertEquals("camp", tasksOnDate.get(0).getActivity());
    }

    @Test
    public void findTasksByDate_duringMultiDayEvent_returnsEvent() {
        TaskList tasks = new TaskList();
        tasks.add(new Event("camp", LocalDate.parse("2026-09-01"), LocalDate.parse("2026-09-03")));

        List<Task> tasksOnDate = tasks.findTasksByDate(LocalDate.parse("2026-09-02"));

        assertEquals(1, tasksOnDate.size());
        assertInstanceOf(Event.class, tasksOnDate.get(0));
        assertEquals("camp", tasksOnDate.get(0).getActivity());
    }

    @Test
    public void findTasksByDate_noMatchingDate_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("submit homework", LocalDate.parse("2026-08-29")));
        tasks.add(new Event("camp", LocalDate.parse("2026-09-01"), LocalDate.parse("2026-09-03")));

        List<Task> tasksOnDate = tasks.findTasksByDate(LocalDate.parse("2026-10-01"));

        assertEquals(0, tasksOnDate.size());
    }

    @Test
    public void findTasksByKeyword_matchingKeyword_returnsMatchingTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("return book", LocalDate.parse("2026-08-29")));
        tasks.add(new Event("camp", LocalDate.parse("2026-09-01"), LocalDate.parse("2026-09-03")));

        List<Task> matchingTasks = tasks.findTasksByKeyword("book");

        assertEquals(2, matchingTasks.size());
        assertEquals("read book", matchingTasks.get(0).getActivity());
        assertEquals("return book", matchingTasks.get(1).getActivity());
    }

    @Test
    public void findTasksByKeyword_differentCase_returnsMatchingTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Read Book"));

        List<Task> matchingTasks = tasks.findTasksByKeyword("book");

        assertEquals(1, matchingTasks.size());
        assertEquals("Read Book", matchingTasks.get(0).getActivity());
    }

    @Test
    public void findTasksByKeyword_noMatchingKeyword_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("submit homework", LocalDate.parse("2026-08-29")));

        List<Task> matchingTasks = tasks.findTasksByKeyword("lecture");

        assertEquals(0, matchingTasks.size());
    }
}
