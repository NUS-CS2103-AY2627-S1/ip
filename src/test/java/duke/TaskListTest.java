package duke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests task-list ordering, mutations, validation, and capacity handling. */
class TaskListTest {
    @Test
    void invalidTaskFields_areRejectedAtEveryCollectionEntryPoint() throws WangsaException {
        List<Task> invalidTasks = List.of(new Todo(null), new Todo("  "),
                new Deadline("missing date", null), new Deadline("negative year", LocalDate.of(-1, 1, 1)),
                new Deadline("five-digit year", LocalDate.of(10000, 1, 1)), new Event("missing start", " ", "4pm"),
                new Event("missing end", "2pm", null));
        TaskList tasks = new TaskList();
        for (Task task : invalidTasks) {
            assertThrows(WangsaException.class, () -> tasks.add(task));
            assertThrows(WangsaException.class, () -> tasks.addAll(new Todo("valid"), task));
            assertThrows(WangsaException.class, () -> new TaskList(List.of(task)));
            assertTrue(tasks.getTasks().isEmpty());
        }
        assertThrows(WangsaException.class, () -> new TaskList(null));
    }

    @Test
    void findMatches_nonAdjacentResults_preservesActionableNumbers() throws WangsaException {
        Task first = new Todo("other");
        Task second = new Todo("read book");
        Task last = new Todo("return book");
        TaskList tasks = new TaskList(List.of(first, second, new Todo("meeting"), last));

        assertEquals(List.of(new TaskMatch(2, second), new TaskMatch(4, last)), tasks.findMatches("BOOK"));
        tasks.mark(tasks.findMatches("return").get(0).taskNumber());
        assertTrue(last.isDone());
        assertFalse(first.isDone());
        tasks.delete(1);
        assertEquals(3, tasks.findMatches("return").get(0).taskNumber());
    }

    @Test
    void addMarkUnmarkAndDelete_preserveTaskStateAndOrder() throws WangsaException {
        TaskList tasks = new TaskList();
        Task todo = new Todo("read book");
        Task deadline = new Deadline("return book", java.time.LocalDate.of(2019, 12, 2));

        assertSame(todo, tasks.add(todo));
        assertSame(deadline, tasks.add(deadline));
        assertEquals(List.of(todo, deadline), tasks.getTasks());

        assertSame(deadline, tasks.mark(2));
        assertTrue(deadline.isDone());
        assertSame(deadline, tasks.unmark(2));
        assertFalse(deadline.isDone());

        assertSame(todo, tasks.delete(1));
        assertEquals(1, tasks.size());
        assertEquals(List.of(deadline), tasks.getTasks());
    }

    @Test
    void invalidTaskNumbers_throwWithoutChangingTheList() throws WangsaException {
        TaskList tasks = new TaskList();
        Task task = new Todo("read book");
        tasks.add(task);

        assertThrows(WangsaException.class, () -> tasks.mark(0));
        assertThrows(WangsaException.class, () -> tasks.unmark(2));
        assertThrows(WangsaException.class, () -> tasks.delete(-1));
        assertEquals(List.of(task), tasks.getTasks());
        assertFalse(task.isDone());
    }

    @Test
    void addingMoreThanOneHundredTasks_throwsAndKeepsCapacity() throws WangsaException {
        TaskList tasks = new TaskList();
        for (int i = 0; i < 100; i++) {
            tasks.add(new Todo("task " + i));
        }

        assertThrows(WangsaException.class, () -> tasks.add(new Todo("overflow")));
        assertEquals(100, tasks.size());
    }

    @Test
    void find_matchesDescriptionCaseInsensitivelyInOriginalOrder() throws WangsaException {
        Task first = new Todo("Read the book");
        Task second = new Todo("Return the book");
        Task other = new Todo("Attend meeting");
        TaskList tasks = new TaskList();
        tasks.add(first);
        tasks.add(second);
        tasks.add(other);

        assertEquals(List.of(first, second), tasks.find("BOOK"));
        assertEquals(List.of(), tasks.find("missing"));
    }

    @Test
    void sortByDeadline_ordersDeadlinesFirstAndKeepsUndatedTasksStable() throws WangsaException {
        Task laterDeadline = new Deadline("later", LocalDate.of(2026, 10, 20));
        Task todo = new Todo("todo");
        Task event = new Event("event", "2pm", "4pm");
        Task earlierDeadline = new Deadline("earlier", LocalDate.of(2026, 9, 20));
        TaskList tasks = new TaskList();
        tasks.addAll(laterDeadline, todo, event, earlierDeadline);

        tasks.sortByDeadline();

        assertEquals(List.of(earlierDeadline, laterDeadline, todo, event), tasks.getTasks());
    }

    @Test
    void addAll_addsBatchAtomicallyAndRejectsOverflow() throws WangsaException {
        TaskList tasks = new TaskList();
        Task first = new Todo("first");
        Task second = new Todo("second");

        tasks.addAll(first, second);
        assertEquals(List.of(first, second), tasks.getTasks());

        for (int i = 2; i < 100; i++) {
            tasks.add(new Todo("task " + i));
        }
        assertThrows(WangsaException.class, () -> tasks.addAll(new Todo("overflow"), new Todo("extra")));
        assertEquals(100, tasks.size());
    }

    @Test
    void addAndAddAll_rejectNullTasksWithoutChangingTheList() throws WangsaException {
        TaskList tasks = new TaskList();

        assertThrows(WangsaException.class, () -> tasks.add(null));
        assertThrows(WangsaException.class, () -> tasks.addAll(new Todo("valid"), null));
        assertTrue(tasks.getTasks().isEmpty());
    }
}
