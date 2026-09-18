package kopi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class TaskListTest {
    @Test
    void find_keywordWithDifferentCase_matchingTasksReturned() {
        TaskList tasks = new TaskList(List.of(
                new Todo("Read book"),
                new Todo("return BOOK"),
                new Todo("buy groceries")));

        List<String> matches = tasks.find("book").stream().map(Task::getDescription).toList();

        assertEquals(List.of("Read book", "return BOOK"), matches);
    }

    @Test
    void find_missingKeyword_emptyListReturned() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertEquals(List.of(), tasks.find("meeting"));
    }

    @Test
    void taskOperations_invalidArguments_assertionThrown() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertThrows(AssertionError.class, () -> tasks.add(null));
        assertThrows(AssertionError.class, () -> tasks.get(1));
        assertThrows(AssertionError.class, () -> tasks.delete(-1));
    }

    @Test
    void constructor_invalidTaskCollections_assertionThrown() {
        assertThrows(AssertionError.class, () -> new TaskList(null));
        assertThrows(AssertionError.class, () -> new TaskList(
                Arrays.asList(new Todo("read book"), null)));
    }

    @Test
    void hasDuplicateOf_sameTypeAndDetails_duplicateDetected() {
        TaskList tasks = new TaskList(List.of(
                new Todo("Read book"),
                new Deadline("return book", LocalDate.parse("2026-09-10")),
                new Event(
                        "project meeting",
                        LocalDate.parse("2026-09-11"),
                        LocalDate.parse("2026-09-12"))));

        assertTrue(tasks.hasDuplicateOf(new Todo("read BOOK")));
        assertTrue(tasks.hasDuplicateOf(
                new Deadline("RETURN BOOK", LocalDate.parse("2026-09-10"))));
        assertTrue(tasks.hasDuplicateOf(new Event(
                "Project Meeting",
                LocalDate.parse("2026-09-11"),
                LocalDate.parse("2026-09-12"))));
    }

    @Test
    void hasDuplicateOf_differentTypeOrDates_duplicateNotDetected() {
        TaskList tasks = new TaskList(List.of(
                new Todo("return book"),
                new Deadline("return book", LocalDate.parse("2026-09-10")),
                new Event(
                        "project meeting",
                        LocalDate.parse("2026-09-11"),
                        LocalDate.parse("2026-09-12"))));

        assertFalse(tasks.hasDuplicateOf(
                new Deadline("return book", LocalDate.parse("2026-09-11"))));
        assertFalse(tasks.hasDuplicateOf(new Event(
                "project meeting",
                LocalDate.parse("2026-09-11"),
                LocalDate.parse("2026-09-13"))));
        assertFalse(tasks.hasDuplicateOf(
                new Deadline("new task", LocalDate.parse("2026-09-10"))));
    }
}
