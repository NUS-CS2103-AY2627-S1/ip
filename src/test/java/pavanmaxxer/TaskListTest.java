package pavanmaxxer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests task-list collection operations.
 */
public class TaskListTest {
    @Test
    void containsSameDetails_todoWithDifferentCase_returnsTrue() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertTrue(tasks.containsSameDetails(new Todo("READ BOOK")));
    }

    @Test
    void containsSameDetails_sameDescriptionDifferentType_returnsFalse() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("return book"));

        assertFalse(tasks.containsSameDetails(new Deadline(
                "return book", LocalDate.parse("2026-09-20"))));
    }

    @Test
    void containsSameDetails_deadlineWithDifferentDate_returnsFalse() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline(
                "return book", LocalDate.parse("2026-09-20")));

        assertFalse(tasks.containsSameDetails(new Deadline(
                "RETURN BOOK", LocalDate.parse("2026-09-21"))));
    }

    @Test
    void containsSameDetails_eventWithDifferentCase_returnsTrue() {
        TaskList tasks = new TaskList();
        tasks.add(new Event("meeting", "2PM", "4PM"));

        assertTrue(tasks.containsSameDetails(
                new Event("MEETING", "2pm", "4pm")));
    }

    @Test
    void containsSameDetails_completedTaskAndIncompleteCandidate_returnsTrue() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.mark(0);

        assertTrue(tasks.containsSameDetails(new Todo("read book")));
    }

    @Test
    void add_nullTask_throwsAssertionError() {
        TaskList tasks = new TaskList();

        assertThrows(AssertionError.class, () -> tasks.add(null));
    }

    @Test
    void mark_indexAtSize_throwsAssertionError() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertThrows(AssertionError.class, () -> tasks.mark(tasks.size()));
    }

    @Test
    void delete_middleTask_closesGap() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("A"));
        tasks.add(new Todo("B"));
        tasks.add(new Todo("C"));

        Task deleted = tasks.delete(1);

        assertEquals("B", deleted.getDescription());
        assertEquals(List.of("A", "C"), tasks.asList().stream()
                .map(Task::getDescription)
                .toList());
    }

    @Test
    void markAndUnmark_validIndex_updatesTaskState() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertTrue(tasks.mark(0).isDone());
        assertFalse(tasks.unmark(0).isDone());
    }

    @Test
    void asList_returnedList_cannotMutateTaskList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertThrows(UnsupportedOperationException.class, () ->
                tasks.asList().add(new Todo("write book")));
        assertEquals(1, tasks.size());
    }

    @Test
    void find_mixedCaseKeyword_returnsMatchesInOriginalOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Read Book"));
        tasks.add(new Todo("buy milk"));
        tasks.add(new Deadline(
                "return book", LocalDate.parse("2026-08-31")));

        assertEquals(List.of("Read Book", "return book"),
                tasks.find("BOOK").stream()
                        .map(Task::getDescription)
                        .toList());
    }

    @Test
    void find_multipleKeywords_returnsTasksContainingEveryKeyword() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("read journal"));
        tasks.add(new Todo("buy book"));

        assertEquals(List.of("read book"),
                tasks.find("READ", "book").stream()
                        .map(Task::getDescription)
                        .toList());
    }
}
