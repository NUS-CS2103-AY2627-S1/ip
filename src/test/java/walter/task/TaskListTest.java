package walter.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import walter.DukeException;

/**
 * Tests task collection operations and capacity rules.
 */
public class TaskListTest {
    @Test
    public void add_multipleTasks_retrievableInOriginalOrder() throws DukeException {
        Task first = new Todo("first");
        Task second = new Todo("second");
        TaskList tasks = new TaskList();

        tasks.add(first);
        tasks.add(second);

        assertEquals(2, tasks.size());
        assertSame(first, tasks.get(0));
        assertSame(second, tasks.get(1));
    }

    @Test
    public void delete_validIndex_correctTaskRemovedAndReturned() throws DukeException {
        Task first = new Todo("first");
        Task second = new Todo("second");
        TaskList tasks = new TaskList(List.of(first, second));

        Task removed = tasks.delete(0);

        assertSame(first, removed);
        assertEquals(1, tasks.size());
        assertSame(second, tasks.get(0));
    }

    @Test
    public void markAndUnmark_validIndex_statusUpdated() throws DukeException {
        Task task = new Todo("read book");
        TaskList tasks = new TaskList(List.of(task));

        assertThrows(DukeException.class, () -> tasks.markAsNotDone(0));
        assertSame(task, tasks.markAsDone(0));
        assertTrue(task.isDone());
        assertThrows(DukeException.class, () -> tasks.markAsDone(0));

        assertSame(task, tasks.markAsNotDone(0));
        assertFalse(task.isDone());
        assertThrows(DukeException.class, () -> tasks.markAsNotDone(0));
    }

    @Test
    public void get_negativeOrOutOfRangeIndex_exceptionThrown() throws DukeException {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertThrows(DukeException.class, () -> tasks.get(-1));
        assertThrows(DukeException.class, () -> tasks.get(1));
        assertThrows(DukeException.class, () -> tasks.delete(1));
        assertThrows(DukeException.class, () -> tasks.markAsDone(-1));
        assertThrows(DukeException.class, () -> tasks.markAsNotDone(1));
    }

    @Test
    public void getDeadlinesOn_multipleDates_onlyMatchesReturnedInOriginalOrder()
            throws DukeException {
        LocalDate targetDate = LocalDate.of(2026, 8, 30);
        Deadline firstMatch = new Deadline("first match", targetDate);
        Todo todo = new Todo("not a deadline");
        Deadline otherDate = new Deadline("other date", LocalDate.of(2026, 8, 31));
        Deadline secondMatch = new Deadline("second match", targetDate);
        TaskList tasks = new TaskList(List.of(firstMatch, todo, otherDate, secondMatch));

        assertEquals(List.of(firstMatch, secondMatch), tasks.getDeadlinesOn(targetDate));
    }

    @Test
    public void find_multipleMatches_matchesCaseInsensitivelyInOriginalOrder()
            throws DukeException {
        Todo firstMatch = new Todo("Read Book");
        Todo nonMatch = new Todo("buy milk");
        Deadline secondMatch = new Deadline("return book", LocalDate.of(2026, 8, 30));
        Event thirdMatch = new Event("BOOK club", "3pm");
        TaskList tasks = new TaskList(List.of(firstMatch, nonMatch, secondMatch, thirdMatch));

        assertEquals(List.of(firstMatch, secondMatch, thirdMatch), tasks.find("bOoK"));
    }

    @Test
    public void find_noMatches_emptyListReturned() throws DukeException {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertTrue(tasks.find("milk").isEmpty());
    }

    @Test
    public void find_keywordInDeadlineMetadata_onlyDescriptionMatchReturned()
            throws DukeException {
        Deadline metadataOnly = new Deadline("submit report", LocalDate.of(2026, 8, 30));
        Todo descriptionMatch = new Todo("plan for 2026");
        TaskList tasks = new TaskList(List.of(metadataOnly, descriptionMatch));

        assertEquals(List.of(descriptionMatch), tasks.find("2026"));
    }

    @Test
    public void add_moreThanCapacity_exceptionThrown() throws DukeException {
        TaskList tasks = new TaskList();
        for (int i = 0; i < 100; i++) {
            tasks.add(new Todo("task " + i));
        }

        assertEquals(100, tasks.size());
        assertThrows(DukeException.class, () -> tasks.add(new Todo("overflow")));
    }

    @Test
    public void constructor_moreThanCapacity_exceptionThrown() {
        List<Task> oversizedTasks = new ArrayList<>();
        for (int i = 0; i < 101; i++) {
            oversizedTasks.add(new Todo("task " + i));
        }

        assertThrows(DukeException.class, () -> new TaskList(oversizedTasks));
    }

    @Test
    public void getTasks_returnedList_cannotMutateInternalList() throws DukeException {
        TaskList tasks = new TaskList(List.of(new Todo("first")));
        List<Task> snapshot = tasks.getTasks();

        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new Todo("second")));

        tasks.add(new Todo("third"));
        assertEquals(1, snapshot.size());
        assertEquals(2, tasks.size());
    }
}
