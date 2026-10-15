package hse.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

class TaskListTest {
    @Test
    void add_nullTask_assertionError() {
        TaskList tasks = new TaskList();

        assertThrows(AssertionError.class, () -> tasks.add(null));
    }

    @Test
    void get_outOfRangeIndex_assertionError() {
        TaskList tasks = new TaskList();

        assertThrows(AssertionError.class, () -> tasks.get(0));
    }

    @Test
    void deleteMarkAndUnmark_existingTask_updatesAndReturnsTask() {
        Task firstTask = new ToDos("read notes");
        Task secondTask = new ToDos("write report");
        TaskList tasks = new TaskList(List.of(firstTask, secondTask));

        assertEquals(firstTask, tasks.mark(0));
        assertEquals("[T][X] read notes", firstTask.toString());
        assertEquals(firstTask, tasks.unmark(0));
        assertEquals("[T][ ] read notes", firstTask.toString());
        assertEquals(secondTask, tasks.delete(1));
        assertEquals(List.of(firstTask), tasks.getTasks());
    }

    @Test
    void find_matchingKeyword_returnsTasksInOriginalOrder() {
        Task firstMatch = new ToDos("read book");
        Task nonMatch = new ToDos("write notes");
        Task secondMatch = new Deadline("return book", LocalDate.of(2026, 9, 3).atStartOfDay());
        TaskList tasks = new TaskList(List.of(firstMatch, nonMatch, secondMatch));

        assertEquals(List.of(firstMatch, secondMatch), tasks.find("book"));
    }

    @Test
    void find_noMatchingKeyword_returnsEmptyList() {
        TaskList tasks = new TaskList(List.of(new ToDos("read notes")));

        assertTrue(tasks.find("book").isEmpty());
    }

    @Test
    void sortByDate_datedTasksFirstAndTodosKeepTheirOrder() {
        Task firstTodo = new ToDos("read notes");
        Task lateDeadline = new Deadline("submit report", LocalDate.of(2026, 9, 9).atStartOfDay());
        Task event = new Event("meeting", LocalDate.of(2026, 9, 4).atTime(14, 0),
                LocalDate.of(2026, 9, 4).atTime(15, 0));
        Task earlyDeadline = new Deadline("buy gift", LocalDate.of(2026, 9, 2).atStartOfDay());
        Task secondTodo = new ToDos("read book");
        TaskList tasks = new TaskList(List.of(firstTodo, lateDeadline, event, earlyDeadline, secondTodo));

        tasks.sortByDate();

        assertEquals(List.of(earlyDeadline, event, lateDeadline, firstTodo, secondTodo), tasks.getTasks());
    }

    @Test
    void getTasksOn_matchingDate_returnsDeadlinesAndEventsButNotTodos() {
        LocalDate targetDate = LocalDate.of(2026, 9, 3);
        Task deadline = new Deadline("submit assignment", targetDate.atStartOfDay());
        Task event = new Event("camp", targetDate.minusDays(1).atTime(18, 0),
                targetDate.plusDays(1).atTime(10, 0));
        Task otherDeadline = new Deadline("later task", targetDate.plusDays(1).atStartOfDay());
        Task todo = new ToDos("read notes");
        TaskList tasks = new TaskList(List.of(deadline, event, otherDeadline, todo));

        assertEquals(List.of(deadline, event), tasks.getTasksOn(targetDate));
        assertEquals(List.of(1, 2), tasks.getTaskPositionsOn(targetDate));
    }

    @Test
    void getTasksOn_noMatchingTasks_returnsEmptyList() {
        LocalDate targetDate = LocalDate.of(2026, 9, 3);
        TaskList tasks = new TaskList(List.of(
                new Deadline("later task", targetDate.plusDays(1).atStartOfDay()),
                new ToDos("read notes")));

        assertTrue(tasks.getTasksOn(targetDate).isEmpty());
        assertTrue(tasks.getTaskPositionsOn(targetDate).isEmpty());
    }
}
