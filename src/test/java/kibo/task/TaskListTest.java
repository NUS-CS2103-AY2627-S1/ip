package kibo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests operations for searching a task list.
 */
public class TaskListTest {

    @Test
    void addAndRemove_firstMiddleAndLast_preservesOrder() {
        TaskList tasks = new TaskList();
        Task first = new Todo("first");
        Task middle = new Todo("middle");
        Task last = new Todo("last");
        tasks.add(last);
        tasks.add(0, first);
        tasks.add(1, middle);
        assertEquals(3, tasks.size());
        assertSame(first, tasks.get(0));
        assertSame(middle, tasks.remove(1));
        assertSame(last, tasks.get(1));
        assertSame(last, tasks.remove(1));
        assertSame(first, tasks.remove(0));
        assertEquals(0, tasks.size());
    }

    @Test
    void constructor_sourceListChanges_doesNotChangeStoredList() {
        Task task = new Todo("original");
        ArrayList<Task> source = new ArrayList<>(List.of(task));
        TaskList tasks = new TaskList(source);
        source.clear();
        assertEquals(1, tasks.size());
        assertSame(task, tasks.get(0));
    }

    @Test
    void access_invalidIndices_throwsWithoutChangingList() {
        TaskList tasks = new TaskList(List.of(new Todo("only")));
        for (int index : new int[]{-1, 1}) {
            assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(index));
            assertThrows(IndexOutOfBoundsException.class, () -> tasks.remove(index));
        }
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.add(2, new Todo("invalid")));
        assertEquals(1, tasks.size());
    }

    @Test
    void iterator_removalAttempt_isReadOnly() {
        TaskList tasks = new TaskList(List.of(new Todo("keep")));
        Iterator<Task> iterator = tasks.iterator();
        assertSame(tasks.get(0), iterator.next());
        assertThrows(UnsupportedOperationException.class, iterator::remove);
        assertFalse(iterator.hasNext());
        assertEquals(1, tasks.size());
    }

    @Test
    void findAndSchedule_resultListChanges_doNotChangeSourceStructure() {
        Task task = new Deadline("read book", LocalDate.of(2024, 2, 29));
        TaskList tasks = new TaskList(List.of(task));
        tasks.find("book").remove(0);
        tasks.findScheduledOn(LocalDate.of(2024, 2, 29)).add(new Todo("extra"));
        assertEquals(1, tasks.size());
        assertSame(task, tasks.get(0));
    }

    @Test
    void findAndSchedule_emptyList_returnsEmptyResults() {
        TaskList tasks = new TaskList();
        assertEquals(0, tasks.find("book").size());
        assertEquals(0, tasks.findScheduledOn(LocalDate.of(2024, 2, 29)).size());
        assertThrows(AssertionError.class, () -> tasks.findScheduledOn(null));
    }

    @Test
    void find_matchingDescriptions_returnsMatchesInOriginalOrder() {
        Task readBook = new Todo("read book");
        Task returnBook = new Deadline("return book", LocalDate.of(2019, 6, 6));
        Task projectMeeting = new Event("project meeting", "2pm", "4pm");
        TaskList tasks = new TaskList(List.of(readBook, returnBook, projectMeeting));

        TaskList matches = tasks.find("book");

        assertEquals(2, matches.size());
        assertSame(readBook, matches.get(0));
        assertSame(returnBook, matches.get(1));
    }

    @Test
    void find_keywordWithDifferentCase_returnsMatch() {
        TaskList tasks = new TaskList(List.of(new Todo("Read Book")));

        TaskList matches = tasks.find("BOOK");

        assertEquals(1, matches.size());
    }

    @Test
    void find_keywordAbsentFromDescriptions_returnsEmptyList() {
        TaskList tasks = new TaskList(List.of(
                new Deadline("return book", LocalDate.of(2019, 6, 6))));

        TaskList matches = tasks.find("2019");

        assertEquals(0, matches.size());
    }

    @Test
    void findScheduledOn_matchingDeadlinesAndDatedEvents_returnsMatchesInOriginalOrder() {
        Task todo = new Todo("read book");
        Task legacyEvent = new Event("legacy meeting", "Mon 2pm", "4pm");
        Task datedEvent = new Event("dated meeting", "2019-12-02 2pm", "4pm");
        Task matchingDeadline = new Deadline("submit report", LocalDate.of(2019, 12, 2));
        Task laterDeadline = new Deadline("return book", LocalDate.of(2019, 12, 3));
        TaskList tasks = new TaskList(List.of(
                todo, legacyEvent, datedEvent, matchingDeadline, laterDeadline));

        TaskList matches = tasks.findScheduledOn(LocalDate.of(2019, 12, 2));

        assertEquals(2, matches.size());
        assertSame(datedEvent, matches.get(0));
        assertSame(matchingDeadline, matches.get(1));
    }

    @Test
    void findScheduledOn_dateWithoutTasks_returnsEmptyList() {
        TaskList tasks = new TaskList(List.of(
                new Deadline("submit report", LocalDate.of(2019, 12, 2)),
                new Event("project meeting", "Mon 2pm", "4pm")));

        TaskList matches = tasks.findScheduledOn(LocalDate.of(2019, 12, 3));

        assertEquals(0, matches.size());
    }
}
