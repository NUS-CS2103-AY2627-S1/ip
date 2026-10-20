package yapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests collection ownership, task updates, and date filtering.
 */
public class TaskListTest {
    @Test
    public void constructor_sourceListChanged_keepsIndependentStructure() {
        Task task = new Todo("book");
        List<Task> source = new ArrayList<>(List.of(task));
        TaskList tasks = new TaskList(source);
        source.clear();
        assertEquals(1, tasks.size());
        assertSame(task, tasks.get(0));
        tasks.add(new Todo("essay"));
        assertEquals(0, source.size());
    }

    @Test
    public void addAndDelete_multipleTasks_preservesOrderAndReturnsRemovedTask() {
        TaskList tasks = new TaskList();
        assertEquals(0, tasks.size());
        Task first = new Todo("first");
        Task second = new Todo("second");
        Task third = new Todo("third");
        tasks.add(first);
        tasks.add(second);
        tasks.add(third);
        assertSame(second, tasks.delete(1));
        assertEquals(List.of(first, third), tasks.asList());
    }

    @Test
    public void markAndUnmark_repeatedCalls_updatesOnlySelectedTask() {
        Task first = new Todo("first");
        Task second = new Todo("second");
        TaskList tasks = new TaskList(List.of(first, second));
        assertSame(second, tasks.mark(1));
        tasks.mark(1);
        assertEquals("X", second.getStatusIcon());
        assertEquals(" ", first.getStatusIcon());
        assertSame(second, tasks.unmark(1));
        tasks.unmark(1);
        assertEquals(" ", second.getStatusIcon());
    }

    @Test
    public void asList_snapshot_isImmutableButSharesTasks() {
        Task task = new Todo("book");
        TaskList tasks = new TaskList(List.of(task));
        List<Task> snapshot = tasks.asList();
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new Todo("essay")));
        tasks.add(new Todo("essay"));
        tasks.mark(0);
        assertEquals(1, snapshot.size());
        assertSame(task, snapshot.get(0));
        assertEquals("X", snapshot.get(0).getStatusIcon());
    }

    @Test
    public void indicesOn_multiDayEvent_includesEndpointsAndPreservesIndices() {
        LocalDate start = LocalDate.of(2024, 2, 28);
        LocalDate end = LocalDate.of(2024, 3, 1);
        TaskList tasks = new TaskList(List.of(new Todo("undated"),
                new Deadline("due", start.atTime(18, 0)),
                new Event("trip", start.atTime(23, 0), end.atStartOfDay())));
        tasks.mark(1);
        assertEquals(List.of(), tasks.indicesOn(start.minusDays(1)));
        assertEquals(List.of(1, 2), tasks.indicesOn(start));
        assertEquals(List.of(2), tasks.indicesOn(start.plusDays(1)));
        assertEquals(List.of(2), tasks.indicesOn(end));
        assertEquals(List.of(), tasks.indicesOn(end.plusDays(1)));
        assertEquals(List.of(), new TaskList().indicesOn(start));
    }
}
