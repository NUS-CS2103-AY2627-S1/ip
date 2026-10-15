package peter.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import peter.exception.PeterException;

public class TaskListTest {

    @Test
    public void delete_validIndex_taskRemovedSuccessfully() throws PeterException {
        TaskList tasks = new TaskList();
        Task todo = new Todo("read book");
        tasks.add(todo);

        Task removed = tasks.delete(0);
        assertEquals(todo, removed);
        assertEquals(0, tasks.size());
    }

    @Test
    public void delete_invalidIndex_exceptionThrown() throws PeterException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertThrows(PeterException.class, () -> {
            tasks.delete(1);
        });

        assertThrows(PeterException.class, () -> {
            tasks.delete(-1);
        });
    }

    @Test
    public void add_duplicateTask_exceptionThrown() throws PeterException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertThrows(PeterException.class, () -> {
            tasks.add(new Todo("READ BOOK"));
        });
        assertEquals(1, tasks.size());
    }

    @Test
    public void mark_validIndex_taskMarkedAsDone() throws PeterException {
        TaskList tasks = new TaskList();
        Task todo = new Todo("read book");
        tasks.add(todo);

        Task marked = tasks.mark(0);
        assertEquals("[T][X] read book", marked.toString());
    }

    @Test
    public void findTasks_matchingKeyword_returnsMatchingTasksIgnoringCase() {
        Task matchingTask = new Todo("Read Book");
        TaskList tasks = new TaskList(List.of(matchingTask, new Todo("buy groceries")));

        assertEquals(List.of(matchingTask), tasks.findTasks("book"));
    }

    @Test
    public void getTasksOnDate_matchingDeadline_returnsMatchingDeadlines() throws PeterException {
        Task matchingDeadline = new Deadline("return book", "2019-12-02 1800");
        TaskList tasks = new TaskList(List.of(
                matchingDeadline,
                new Deadline("submit report", "2019-12-03 1800"),
                new Todo("plan December 2 activities")));

        assertEquals(List.of(matchingDeadline), tasks.getTasksOnDate(LocalDate.of(2019, 12, 2)));
    }

    @Test
    public void sortByDeadline_mixedTasks_deadlinesSortedBeforeOtherTasks() throws PeterException {
        Task laterDeadline = new Deadline("submit report", "2019-12-03 1800");
        Task firstTodo = new Todo("buy groceries");
        Task earlierDeadline = new Deadline("return book", "2019-12-02 1800");
        Task secondTodo = new Todo("read notes");
        TaskList tasks = new TaskList(List.of(laterDeadline, firstTodo, earlierDeadline, secondTodo));

        tasks.sortByDeadline();

        assertEquals(List.of(earlierDeadline, laterDeadline, firstTodo, secondTodo), tasks.getTasks());
    }

    @Test
    public void constructorAndGetTasks_useDefensiveCopies() {
        List<Task> original = new java.util.ArrayList<>();
        original.add(new Todo("read book"));
        TaskList tasks = new TaskList(original);

        original.clear();

        assertEquals(1, tasks.size());
        assertThrows(UnsupportedOperationException.class, () -> tasks.getTasks().clear());
    }

    @Test
    public void emptySizeAndGet_reportCurrentState() throws PeterException {
        TaskList tasks = new TaskList();

        assertTrue(tasks.isEmpty());
        assertEquals(0, tasks.size());

        Task todo = new Todo("read book");
        tasks.add(todo);
        assertFalse(tasks.isEmpty());
        assertEquals(todo, tasks.get(0));
    }

    @Test
    public void unmark_markedTask_taskBecomesIncomplete() throws PeterException {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");
        todo.markAsDone();
        tasks.add(todo);

        Task unmarked = tasks.unmark(0);

        assertEquals("[T][ ] read book", unmarked.toString());
    }

    @Test
    public void unmark_invalidIndex_exceptionThrown() {
        TaskList tasks = new TaskList();

        assertThrows(PeterException.class, () -> tasks.unmark(0));
    }

    @Test
    public void add_nullTask_exceptionThrown() {
        TaskList tasks = new TaskList();

        assertThrows(PeterException.class, () -> tasks.add(null));
    }

    @Test
    public void add_sameDescriptionDifferentTypeOrSchedule_success() throws PeterException {
        TaskList tasks = new TaskList();

        tasks.add(new Todo("meeting"));
        tasks.add(new Deadline("meeting", "2026-09-10 1200"));
        tasks.add(new Deadline("meeting", "2026-09-10 1300"));
        tasks.add(new Event("meeting", "0900", "1000"));
        tasks.add(new Event("meeting", "1000", "1100"));

        assertEquals(5, tasks.size());
    }

    @Test
    public void findTasks_noMatch_returnsEmptyList() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertTrue(tasks.findTasks("groceries").isEmpty());
    }

    @Test
    public void getTasksOnDate_ignoresTodosAndOtherDates() throws PeterException {
        TaskList tasks = new TaskList(List.of(
                new Todo("plan day"),
                new Deadline("submit", "2026-09-11 1200")));

        assertTrue(tasks.getTasksOnDate(LocalDate.of(2026, 9, 10)).isEmpty());
    }

    @Test
    public void sortByDeadline_emptyList_remainsEmpty() {
        TaskList tasks = new TaskList();

        tasks.sortByDeadline();

        assertTrue(tasks.isEmpty());
    }
}
