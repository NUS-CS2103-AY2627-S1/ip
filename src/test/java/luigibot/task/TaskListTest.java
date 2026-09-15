package luigibot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests date-based searching in {@link TaskList}.
 */
public class TaskListTest {

    @Test
    public void constructor_nullTaskCollection_assertionThrown() {
        assertThrows(AssertionError.class, () -> new TaskList(null));
    }

    @Test
    public void constructor_collectionContainingNull_assertionThrown() {
        List<Task> sourceTasks = new ArrayList<>();
        sourceTasks.add(null);

        assertThrows(AssertionError.class, () -> new TaskList(sourceTasks));
    }

    @Test
    public void constructor_sourceListChanged_taskListUnaffected() {
        List<Task> sourceTasks = new ArrayList<>();
        sourceTasks.add(new Todo("read book"));
        TaskList tasks = new TaskList(sourceTasks);

        sourceTasks.add(new Todo("return book"));

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.getTasks().get(0).toString());
    }

    @Test
    public void add_validTask_taskAppendedAndSizeIncreased() {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");

        tasks.add(todo);

        assertEquals(1, tasks.size());
        assertSame(todo, tasks.getTasks().get(0));
    }

    @Test
    public void add_nullTask_assertionThrown() {
        TaskList tasks = new TaskList();

        assertThrows(AssertionError.class, () -> tasks.add(null));
    }

    @Test
    public void delete_middleTask_correctTaskRemovedAndReturned() {
        Todo firstTask = new Todo("read book");
        Todo secondTask = new Todo("return book");
        Todo thirdTask = new Todo("borrow book");
        TaskList tasks = new TaskList(List.of(firstTask, secondTask, thirdTask));

        Task deletedTask = tasks.delete(2);

        assertSame(secondTask, deletedTask);
        assertEquals(List.of(firstTask, thirdTask), tasks.getTasks());
        assertEquals(2, tasks.size());
    }

    @Test
    public void delete_invalidTaskNumber_assertionThrown() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertThrows(AssertionError.class, () -> tasks.delete(0));
        assertThrows(AssertionError.class, () -> tasks.delete(2));
    }

    @Test
    public void mark_secondTask_correctTaskMarkedAndReturned() {
        Todo firstTask = new Todo("read book");
        Todo secondTask = new Todo("return book");
        TaskList tasks = new TaskList(List.of(firstTask, secondTask));

        Task markedTask = tasks.mark(2);

        assertSame(secondTask, markedTask);
        assertEquals("[ ]", firstTask.getStatusIcon());
        assertEquals("[X]", secondTask.getStatusIcon());
    }

    @Test
    public void unmark_secondTask_correctTaskUnmarkedAndReturned() {
        Todo firstTask = new Todo("read book");
        Todo secondTask = new Todo("return book");
        firstTask.mark();
        secondTask.mark();
        TaskList tasks = new TaskList(List.of(firstTask, secondTask));

        Task unmarkedTask = tasks.unmark(2);

        assertSame(secondTask, unmarkedTask);
        assertEquals("[X]", firstTask.getStatusIcon());
        assertEquals("[ ]", secondTask.getStatusIcon());
    }

    @Test
    public void markAndUnmark_invalidTaskNumber_assertionThrown() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertThrows(AssertionError.class, () -> tasks.mark(0));
        assertThrows(AssertionError.class, () -> tasks.unmark(2));
    }

    @Test
    public void isValidTaskNumber_boundaryAndOutOfRangeNumbers_correctResultReturned() {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"),
                new Todo("return book")));

        assertFalse(tasks.isValidTaskNumber(-1));
        assertFalse(tasks.isValidTaskNumber(0));
        assertTrue(tasks.isValidTaskNumber(1));
        assertTrue(tasks.isValidTaskNumber(2));
        assertFalse(tasks.isValidTaskNumber(3));
    }

    @Test
    public void getTasks_taskAdded_viewUpdatedButCannotBeModifiedExternally() {
        TaskList tasks = new TaskList();
        List<Task> taskView = tasks.getTasks();
        Todo todo = new Todo("read book");

        tasks.add(todo);

        assertEquals(List.of(todo), taskView);
        assertThrows(UnsupportedOperationException.class, () ->
                taskView.add(new Todo("return book")));
    }

    @Test
    public void findIndexesOnDate_emptyTaskList_emptyListReturned() {
        TaskList tasks = new TaskList();

        assertEquals(List.of(), tasks.findIndexesOnDate(LocalDate.of(2026, 8, 27)));
    }

    @Test
    public void findIndexesOnDate_nullDate_assertionThrown() {
        TaskList tasks = new TaskList();

        assertThrows(AssertionError.class, () -> tasks.findIndexesOnDate(null));
    }

    @Test
    public void findIndexesOnDate_multipleMatchingTasks_zeroBasedIndexesInListOrderReturned() {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"),
                new Deadline("submit report", "2026-08-27 1800"),
                new Event("conference", "2026-08-26 2300", "2026-08-28 0100"),
                new Deadline("return book", "2026-08-28 1800")));

        assertEquals(List.of(1, 2),
                tasks.findIndexesOnDate(LocalDate.of(2026, 8, 27)));
    }

    @Test
    public void findIndexesOnDate_noMatchingTasks_emptyListReturned() {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"),
                new Deadline("return book", "2026-08-28 1800"),
                new Event("meeting", "2026-08-28 1400", "2026-08-28 1600")));

        assertEquals(List.of(), tasks.findIndexesOnDate(LocalDate.of(2026, 8, 27)));
    }

    @Test
    public void findIndexesByKeyword_caseInsensitiveMatchesInListOrderReturned() {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"),
                new Deadline("submit report", "2026-08-27 1800"),
                new Todo("Book a table"),
                new Event("team meeting", "2026-08-27 1400", "2026-08-27 1600")));

        assertEquals(List.of(0, 2), tasks.findIndexesByKeyword("BOOK"));
    }

    @Test
    public void findIndexesByKeyword_noMatches_emptyListReturned() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertEquals(List.of(), tasks.findIndexesByKeyword("report"));
    }

    @Test
    public void findIndexesByKeyword_nullOrBlankKeyword_assertionThrown() {
        TaskList tasks = new TaskList();

        assertThrows(AssertionError.class, () -> tasks.findIndexesByKeyword(null));
        assertThrows(AssertionError.class, () -> tasks.findIndexesByKeyword("   "));
    }

    @Test
    public void findIndexesClashingWith_multipleOverlappingEvents_zeroBasedIndexesInListOrderReturned() {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"),
                new Event("team meeting", "2026-08-27 0900", "2026-08-27 1000"),
                new Deadline("submit report", "2026-08-27 1000"),
                new Event("project meeting", "2026-08-27 1000", "2026-08-27 1100")));
        Event newEvent = new Event("design review", "2026-08-27 0930", "2026-08-27 1030");

        assertEquals(List.of(1, 3), tasks.findIndexesClashingWith(newEvent));
    }

    @Test
    public void findIndexesClashingWith_noOverlappingEvents_emptyListReturned() {
        TaskList tasks = new TaskList(List.of(
                new Event("team meeting", "2026-08-27 0900", "2026-08-27 1000")));
        Event newEvent = new Event("project meeting", "2026-08-27 1000", "2026-08-27 1100");

        assertEquals(List.of(), tasks.findIndexesClashingWith(newEvent));
    }
}
