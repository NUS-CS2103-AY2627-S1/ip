package friday.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests one-based selection, mutation feedback, collection ownership, and date queries.
 */
class TaskListTest {
    @Test
    void addAndDelete_mixedTaskTypes_preservesOrderAndRenumbersTasks() {
        TaskList tasks = new TaskList();
        Task todo = new ToDo("read book");
        Task deadline = new Deadline("return book", "2019-12-02");
        Task event = new Event("meeting", "2019-12-02 14:00", "2019-12-02 16:00");
        tasks.add(todo);
        tasks.add(deadline);
        tasks.add(event);
        assertEquals(List.of(todo, deadline, event), tasks.toList());
        assertSame(todo, tasks.get(1));
        assertSame(event, tasks.get(3));

        assertSame(deadline, tasks.delete(2));
        assertEquals(List.of(todo, event), tasks.toList());
        assertSame(event, tasks.get(2));
        assertSame(todo, tasks.delete(1));
        assertSame(event, tasks.get(1));
        assertSame(event, tasks.delete(1));
        assertEquals(0, tasks.size());
    }

    @Test
    void taskNumber_emptyList_rejectsEverySelection() {
        TaskList tasks = new TaskList();
        assertFalse(tasks.isValidTaskNumber(1));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.delete(1));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.mark(1));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.unmark(1));
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -1, 0, 2, Integer.MAX_VALUE})
    void taskNumber_outOfRange_throwsWithoutChangingTasks(int number) {
        Task task = new ToDo("keep me");
        TaskList tasks = new TaskList(List.of(task));
        assertTrue(tasks.isValidTaskNumber(1));
        assertFalse(tasks.isValidTaskNumber(number));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(number));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.delete(number));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.mark(number));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.unmark(number));
        assertEquals(List.of(task), tasks.toList());
        assertFalse(task.isDone());
    }

    @Test
    void markAndUnmark_repeatedCalls_onlyReportActualChanges() {
        TaskList tasks = new TaskList(List.of(new ToDo("first"), new ToDo("second")));
        assertFalse(tasks.unmark(2));
        assertTrue(tasks.mark(2));
        assertTrue(tasks.get(2).isDone());
        assertFalse(tasks.get(1).isDone(), "Only the selected task should change.");
        assertFalse(tasks.mark(2));
        assertTrue(tasks.unmark(2));
        assertFalse(tasks.get(2).isDone());
        assertFalse(tasks.unmark(2));
        assertFalse(tasks.get(1).isDone());
    }

    @Test
    void constructor_sourceListChanges_doesNotChangeOwnedMembership() {
        Task completed = new ToDo("already finished");
        completed.markAsDone();
        List<Task> loadedTasks = new ArrayList<>(List.of(completed));
        TaskList tasks = new TaskList(loadedTasks);
        loadedTasks.clear();
        assertEquals(List.of(completed), tasks.toList());
        assertTrue(tasks.get(1).isDone());
        tasks.add(new ToDo("new task"));
        assertEquals(2, tasks.size());
    }

    @Test
    void toList_laterChanges_preservesSnapshotMembershipButSharesTaskObjects() {
        Task original = new ToDo("original");
        TaskList tasks = new TaskList(List.of(original));
        List<Task> snapshotTasks = tasks.toList();
        assertThrows(UnsupportedOperationException.class, () -> snapshotTasks.add(new ToDo("external")));
        assertThrows(UnsupportedOperationException.class, snapshotTasks::clear);
        tasks.mark(1);
        assertSame(original, snapshotTasks.getFirst());
        assertTrue(snapshotTasks.getFirst().isDone(), "The snapshot is shallow, as documented.");
        tasks.add(new ToDo("new task"));
        tasks.delete(1);
        assertEquals(List.of(original), snapshotTasks);
        assertEquals("new task", tasks.get(1).getDescription());
    }

    @Test
    void findTaskNumbersOn_mixedDates_returnsOriginalNumbersAndReflectsDeletion() {
        LocalDate day = LocalDate.of(2019, 12, 2);
        TaskList tasks = new TaskList(List.of(
                new ToDo("buy bread"), new Deadline("return book", "2019-12-02 18:00"),
                new Deadline("later", "2019-12-04"),
                new Event("conference", "2019-12-01 10:00", "2019-12-03 00:00")));
        tasks.mark(4);
        List<Task> tasksBeforeQuery = tasks.toList();
        assertEquals(List.of(2, 4), tasks.findTaskNumbersOn(day));
        assertEquals(tasksBeforeQuery, tasks.toList());
        assertFalse(tasks.get(2).isDone());
        assertTrue(tasks.get(4).isDone());
        tasks.delete(1);
        assertEquals(List.of(1, 3), tasks.findTaskNumbersOn(day));
        assertTrue(tasks.findTaskNumbersOn(day.plusDays(10)).isEmpty());
        assertEquals(3, tasks.size());
        assertTrue(tasks.get(3).isDone());
    }

    @Test
    void findTaskNumbersOn_emptyList_returnsEmptyList() {
        assertTrue(new TaskList().findTaskNumbersOn(LocalDate.of(2019, 12, 2)).isEmpty());
    }

    @Test
    void findTaskNumbersContaining_mixedTasks_preservesNumbersAndStatus() {
        TaskList tasks = new TaskList(List.of(new ToDo("bread"), new ToDo("read book"),
                new Deadline("return book", "2019-12-02"),
                new Event("book club", "2019-12-02", "2019-12-03"), new ToDo("bookshelf")));
        tasks.mark(2);
        tasks.mark(3);
        List<Task> tasksBeforeSearch = tasks.toList();

        List<Integer> matches = tasks.findTaskNumbersContaining("book");
        assertEquals(List.of(2, 3, 4, 5), matches);
        assertEquals(tasksBeforeSearch, tasks.toList());
        assertTrue(tasks.get(2).isDone());
        assertTrue(tasks.get(3).isDone());
        assertFalse(tasks.get(4).isDone());
        matches.clear();
        assertEquals(List.of(2, 3, 4, 5), tasks.findTaskNumbersContaining("book"));

        tasks.delete(1);
        assertEquals(List.of(1, 2, 3, 4), tasks.findTaskNumbersContaining("book"));
    }

    @Test
    void findTaskNumbersContaining_casePhraseAndSymbols_matchesLiteralDescriptionsOnly() {
        TaskList tasks = new TaskList(List.of(new ToDo("Read Book"), new ToDo("read book"),
                new ToDo("read  book"), new ToDo("literal .* 读书"),
                new Deadline("return notes", "2019-12-02")));

        assertEquals(List.of(1), tasks.findTaskNumbersContaining("Book"));
        assertEquals(List.of(2), tasks.findTaskNumbersContaining("read book"));
        assertEquals(List.of(3), tasks.findTaskNumbersContaining("read  book"));
        assertEquals(List.of(4), tasks.findTaskNumbersContaining(".*"));
        assertEquals(List.of(4), tasks.findTaskNumbersContaining("读书"));
        assertTrue(tasks.findTaskNumbersContaining("Dec").isEmpty());
        assertTrue(tasks.findTaskNumbersContaining("[D]").isEmpty());
        assertTrue(tasks.findTaskNumbersContaining("missing").isEmpty());
        assertTrue(new TaskList().findTaskNumbersContaining("book").isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "\t", "\u2003"})
    void findTaskNumbersContaining_blankKeyword_throwsWithoutChangingTasks(String keyword) {
        TaskList tasks = new TaskList(List.of(new ToDo("keep book")));
        assertThrows(IllegalArgumentException.class, () -> tasks.findTaskNumbersContaining(keyword));
        assertEquals(1, tasks.size());
        assertFalse(tasks.get(1).isDone());
    }
}
