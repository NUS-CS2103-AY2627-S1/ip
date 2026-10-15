package orbit.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests task collection operations performed by {@link TaskList}.
 */
class TaskListTest {
    /**
     * Verifies that matching tasks retain their original order.
     */
    @Test
    void find_multipleMatchingDescriptions_returnsMatchesInOriginalOrder() {
        Task readBook = new Todo("read book");
        Task returnBook = new Deadline("return book", LocalDate.parse("2019-12-02"));
        Task projectMeeting = new Event("project meeting",
                LocalDate.parse("2019-12-03"), LocalDate.parse("2019-12-04"));
        TaskList tasks = new TaskList(List.of(readBook, returnBook, projectMeeting));

        List<Task> matches = tasks.find("book");

        assertEquals(List.of(readBook, returnBook), matches);
    }

    /**
     * Verifies that find searches descriptions rather than formatted task metadata.
     */
    @Test
    void find_keywordOnlyInDateOrType_doesNotMatch() {
        TaskList tasks = new TaskList(List.of(
                new Deadline("return notes", LocalDate.parse("2019-12-02")),
                new Todo("read notes")));

        assertTrue(tasks.find("2019").isEmpty());
        assertTrue(tasks.find("[T]").isEmpty());
    }

    /**
     * Verifies that find uses case-sensitive substring matching and returns an empty list when appropriate.
     */
    @Test
    void find_differentCaseOrAbsentKeyword_returnsNoMatches() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertTrue(tasks.find("Book").isEmpty());
        assertTrue(tasks.find("homework").isEmpty());
    }

    /**
     * Verifies that schedules include exact deadlines and events spanning the requested date.
     */
    @Test
    void getSchedule_datedTasks_returnsTasksOccurringOnDate() {
        Task todo = new Todo("read book");
        Task deadline = new Deadline("return book", LocalDate.parse("2019-12-02"));
        Task spanningEvent = new Event("camp",
                LocalDate.parse("2019-12-01"), LocalDate.parse("2019-12-03"));
        Task laterEvent = new Event("meeting",
                LocalDate.parse("2019-12-03"), LocalDate.parse("2019-12-03"));
        TaskList tasks = new TaskList(List.of(todo, deadline, spanningEvent, laterEvent));

        assertEquals(List.of(deadline, spanningEvent),
                tasks.getSchedule(LocalDate.parse("2019-12-02")));
    }

    /**
     * Verifies that adding and deleting tasks updates the collection and returns the removed task.
     */
    @Test
    void addAndDelete_validTasks_updatesCollection() {
        Task readBook = new Todo("read book");
        Task returnBook = new Todo("return book");
        TaskList tasks = new TaskList();

        tasks.add(readBook);
        tasks.add(returnBook);

        assertEquals(2, tasks.size());
        assertEquals(List.of(readBook, returnBook), tasks.getTasks());
        assertEquals(readBook, tasks.delete(1));
        assertEquals(List.of(returnBook), tasks.getTasks());
    }

    /**
     * Verifies that marking and unmarking a numbered task updates its displayed status.
     */
    @Test
    void markAndUnmark_validTask_updatesDisplayedStatus() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertEquals("[T][X] read book", tasks.mark(1));
        assertEquals("[T][ ] read book", tasks.unmark(1));
    }

}
