package clarry.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;

import clarry.ClarryException;

/** Tests task collection and date-filtering behavior. */
class TaskListTest {
    @Test
    void constructor_sourceListChanges_doesNotChangeTaskCollection() {
        Todo book = new Todo("book");
        List<Task> source = new ArrayList<>(List.of(book));
        TaskList tasks = new TaskList(source);
        source.clear();
        assertEquals(1, tasks.size());
        assertEquals(book, tasks.get(0));
    }

    @Test
    void delete_firstMiddleAndLast_preservesRemainingOrder() {
        Todo first = new Todo("first");
        Todo middle = new Todo("middle");
        Todo last = new Todo("last");
        TaskList tasks = new TaskList(List.of(first, middle, last));
        assertEquals(middle, tasks.delete(1));
        assertEquals(List.of(first, last), tasks.getTasks());
        assertEquals(first, tasks.delete(0));
        assertEquals(last, tasks.delete(0));
        assertEquals(0, tasks.size());
    }

    @Test
    void iterator_laterChanges_keepsOriginalSnapshotAndRejectsRemoval() {
        Todo first = new Todo("first");
        TaskList tasks = new TaskList(List.of(first));
        var iterator = tasks.iterator();
        List<Task> snapshot = tasks.getTasks();
        tasks.add(new Todo("second"));
        tasks.delete(0);
        assertEquals(first, iterator.next());
        assertFalse(iterator.hasNext());
        assertThrows(UnsupportedOperationException.class, iterator::remove);
        assertEquals(List.of(first), snapshot);
    }

    @Test
    @ResourceLock("defaultLocale")
    void find_turkishLocale_usesLocaleIndependentMatching() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            Todo task = new Todo("VISIT island");
            assertEquals(List.of(task), new TaskList(List.of(task)).find("visit"));
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    void getTasksOnDate_returnsMatchingDeadlineAndEventInOriginalOrder() throws ClarryException {
        LocalDate date = LocalDate.of(2026, 8, 27);
        Deadline deadline = new Deadline("submit report", "2026-08-27");
        Todo todo = new Todo("buy milk");
        Event event = new Event("team meeting", "2026-08-27 10:00", "2026-08-27 11:00");
        TaskList taskList = new TaskList(List.of(deadline, todo, event));

        assertEquals(List.of(deadline, event), taskList.getTasksOnDate(date));
    }

    @Test
    void getTasksOnDate_withoutMatchingTasks_returnsEmptyList() throws ClarryException {
        TaskList taskList = new TaskList(List.of(new Todo("buy milk")));

        assertEquals(List.of(), taskList.getTasksOnDate(LocalDate.of(2026, 8, 27)));
    }

    @Test
    void getTasks_returnsUnmodifiableSnapshot() throws ClarryException {
        TaskList taskList = new TaskList(List.of(new Todo("buy milk")));

        assertThrows(UnsupportedOperationException.class, () -> taskList.getTasks().add(new Todo("read book")));
        assertEquals(1, taskList.size());
    }

    @Test
    void find_caseInsensitiveKeyword_returnsMatchingTasksInOrder() throws ClarryException {
        Todo firstMatch = new Todo("Read the book");
        Todo secondMatch = new Todo("Return the BOOK");
        TaskList taskList = new TaskList(List.of(firstMatch, new Todo("Buy milk"), secondMatch));

        assertEquals(List.of(firstMatch, secondMatch), taskList.find("book"));
    }

    @Test
    void find_noMatchingDescriptions_returnsEmptyList() throws ClarryException {
        TaskList taskList = new TaskList(List.of(new Todo("Buy milk")));

        assertEquals(List.of(), taskList.find("book"));
    }
}
