package chimpanzinibananini.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import chimpanzinibananini.exception.ChimpanziniBananiniException;

/** Tests task-list ownership and one-based task operations. */
class TaskListTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 9, 9, 15, 0);

    @Test
    void getUpcomingDeadlines_windowBoundaries_includesOnlyInclusiveWindow() {
        Deadline start = new Deadline("now", NOW);
        Deadline afterStart = new Deadline("after start", NOW.plusNanos(1));
        Deadline inside = new Deadline("inside", NOW.plusDays(3));
        Deadline beforeEnd = new Deadline("before end", NOW.plusDays(7).minusNanos(1));
        Deadline end = new Deadline("end", NOW.plusDays(7));
        TaskList tasks = new TaskList(List.of(
                new Deadline("overdue", NOW.minusDays(1)),
                new Deadline("before start", NOW.minusNanos(1)), start, afterStart, inside, beforeEnd, end,
                new Deadline("after end", NOW.plusDays(7).plusNanos(1)),
                new Deadline("beyond", NOW.plusDays(8))));

        assertEquals(List.of(start, afterStart, inside, beforeEnd, end), tasks.getUpcomingDeadlines(NOW));
    }

    @Test
    void getUpcomingDeadlines_ineligibleTasks_returnsEmptyList() {
        Deadline completed = new Deadline("completed", NOW.plusDays(1));
        completed.markAsDone();
        TaskList tasks = new TaskList(List.of(completed, new Todo("todo"),
                new Event("event", NOW.toString(), NOW.plusDays(1).toString())));

        assertEquals(List.of(), tasks.getUpcomingDeadlines(NOW));
        assertEquals(List.of(), new TaskList().getUpcomingDeadlines(NOW));
    }

    @Test
    void getUpcomingDeadlines_unsortedTasks_sortsStablyWithoutMutatingTasks() {
        Deadline late = new Deadline("late", NOW.plusDays(5));
        Deadline firstTie = new Deadline("first tie", NOW.plusDays(2));
        Deadline early = new Deadline("early", NOW.plusDays(1));
        Deadline secondTie = new Deadline("second tie", NOW.plusDays(2));
        Deadline completed = new Deadline("completed", NOW);
        completed.markAsDone();
        List<Task> original = List.of(late, firstTie, completed, early, secondTie);
        TaskList tasks = new TaskList(original);
        List<String> originalData = tasks.asList().stream().map(Task::toDataString).toList();

        assertEquals(List.of(early, firstTie, secondTie, late), tasks.getUpcomingDeadlines(NOW));
        assertEquals(original, tasks.asList());
        assertEquals(originalData, tasks.asList().stream().map(Task::toDataString).toList());
    }

    @Test
    void getUpcomingDeadlines_dateOnlyToday_preservesMidnightSemantics() {
        Deadline today = new Deadline("today", "2026-09-09");
        Deadline tomorrow = new Deadline("tomorrow", "2026-09-10");
        TaskList tasks = new TaskList(List.of(today, tomorrow));

        assertEquals(List.of(tomorrow), tasks.getUpcomingDeadlines(NOW));
    }

    @Test
    void constructor_sourceListLaterModified_keepsIndependentCopy() {
        ArrayList<Task> source = new ArrayList<>(List.of(new Todo("read book")));
        TaskList tasks = new TaskList(source);

        source.clear();

        assertEquals(1, tasks.size());
    }

    @Test
    void add_task_appendsTask() {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");

        tasks.add(todo);

        assertEquals(1, tasks.size());
        assertSame(todo, tasks.get(0));
    }

    @Test
    void mark_validOneBasedNumber_marksAndReturnsSelectedTask()
            throws ChimpanziniBananiniException {
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        TaskList tasks = new TaskList(List.of(first, second));

        Task marked = tasks.mark(2);

        assertSame(second, marked);
        assertEquals(" ", first.getStatusIcon());
        assertEquals("X", second.getStatusIcon());
    }

    @Test
    void mark_numberOutsideList_throwsChatbotException() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertThrows(ChimpanziniBananiniException.class, () -> tasks.mark(0));
        assertThrows(ChimpanziniBananiniException.class, () -> tasks.mark(2));
    }

    @Test
    void delete_validOneBasedNumber_removesAndReturnsSelectedTask()
            throws ChimpanziniBananiniException {
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        TaskList tasks = new TaskList(List.of(first, second));

        Task deleted = tasks.delete(1);

        assertSame(first, deleted);
        assertEquals(1, tasks.size());
        assertSame(second, tasks.get(0));
    }

    @Test
    void delete_numberOutsideList_throwsChatbotException() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertThrows(ChimpanziniBananiniException.class, () -> tasks.delete(0));
        assertThrows(ChimpanziniBananiniException.class, () -> tasks.delete(2));
    }

    @Test
    void find_keywordInDescriptions_returnsMatchingTasksInOriginalOrder() {
        Todo firstMatch = new Todo("read book");
        Todo nonMatch = new Todo("write essay");
        Todo secondMatch = new Todo("return book");
        TaskList tasks = new TaskList(List.of(firstMatch, nonMatch, secondMatch));

        List<Task> matches = tasks.find("book");

        assertEquals(List.of(firstMatch, secondMatch), matches);
    }

    @Test
    void find_keywordNotInDescriptions_returnsEmptyList() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertEquals(List.of(), tasks.find("essay"));
    }

    @Test
    void asList_callerAttemptsModification_throwsUnsupportedOperationException() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertThrows(UnsupportedOperationException.class,
                () -> tasks.asList().add(new Todo("write book")));
        assertEquals(1, tasks.size());
    }
}
