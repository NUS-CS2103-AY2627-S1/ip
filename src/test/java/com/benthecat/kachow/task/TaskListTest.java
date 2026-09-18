package com.benthecat.kachow.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import com.benthecat.kachow.exception.KachowException;
import com.benthecat.kachow.parser.DateTimeParser;

/**
 * Tests task state changes, deletion, renumbering, and task lookup.
 */
class TaskListTest {
    /** Verifies that marking and unmarking changes only the selected task. */
    @Test
    void markAndUnmark_validTaskNumber_updateOnlySelectedTask() throws KachowException {
        Todo first = new Todo("read book");
        Deadline second = new Deadline("return book", LocalDate.of(2019, 12, 2));
        TaskList tasks = new TaskList(List.of(first, second));

        Task marked = tasks.mark(2);

        assertSame(second, marked);
        assertFalse(first.isDone());
        assertTrue(second.isDone());

        Task unmarked = tasks.unmark(2);

        assertSame(second, unmarked);
        assertFalse(first.isDone());
        assertFalse(second.isDone());
    }

    /** Verifies deletion at both ends while preserving the remaining task's state. */
    @Test
    void delete_firstAndLastTasks_renumbersWithoutChangingRemainingState() throws KachowException {
        Todo first = new Todo("pole position");
        Deadline second = new Deadline("refuel", LocalDate.of(2026, 8, 20), true);
        Todo third = new Todo("victory lap");
        TaskList tasks = new TaskList(List.of(first, second, third));

        assertSame(first, tasks.delete(1));
        assertSame(third, tasks.delete(2));

        assertEquals(1, tasks.getSize());
        assertSame(second, tasks.getTasks().getFirst());
        assertTrue(tasks.getTasks().getFirst().isDone());
    }

    /** Verifies that an out-of-range deletion fails without mutating the list. */
    @Test
    void delete_outOfRangeTask_throwsWithoutChangingList() {
        Todo task = new Todo("tire change", true);
        TaskList tasks = new TaskList(List.of(task));

        KachowException exception = assertThrows(KachowException.class, () -> tasks.delete(2));

        assertEquals("Racer 2 isn't on the grid. Use list to check the task numbers.", exception.getMessage());
        assertEquals(List.of(task), tasks.getTasks());
        assertTrue(task.isDone());
    }

    /** Verifies that replacement keeps the edited task's original list position. */
    @Test
    void replace_middleTask_changesOnlyTaskAtSelectedPosition() throws KachowException {
        Todo first = new Todo("qualifying");
        Todo original = new Todo("old details", true);
        Todo third = new Todo("podium");
        Todo replacement = new Todo("new details", true);
        TaskList tasks = new TaskList(List.of(first, original, third));

        Task edited = tasks.replace(2, replacement);

        assertSame(replacement, edited);
        assertEquals(List.of(first, replacement, third), tasks.getTasks());
    }

    /** Verifies date lookup, spanning events, original numbering, and todo exclusion. */
    @Test
    void findOn_deadlinesAndSpanningEvents_keepOriginalTaskNumbersAndExcludeTodos() {
        Todo todo = new Todo("wash car");
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2019, 12, 3, 9, 0));
        Event event = new Event(
                "conference",
                new DateTimeParser.ParsedDateTime(
                        LocalDateTime.of(2019, 12, 3, 23, 0)),
                new DateTimeParser.ParsedDateTime(
                        LocalDateTime.of(2019, 12, 4, 1, 0)));
        TaskList tasks = new TaskList(List.of(todo, deadline, event));

        List<TaskList.NumberedTask> firstDay = tasks.findOn(LocalDate.of(2019, 12, 3));
        List<TaskList.NumberedTask> secondDay = tasks.findOn(LocalDate.of(2019, 12, 4));

        assertEquals(List.of(2, 3), firstDay.stream().map(TaskList.NumberedTask::number).toList());
        assertEquals(List.of(deadline, event), firstDay.stream().map(TaskList.NumberedTask::task).toList());
        assertEquals(List.of(3), secondDay.stream().map(TaskList.NumberedTask::number).toList());
        assertTrue(tasks.findOn(LocalDate.of(2019, 12, 5)).isEmpty());
    }

    @Test
    void findByDescription_partialKeywordIgnoringCase_returnsAllTypesWithOriginalNumbers() {
        Todo first = new Todo("Read Book", true);
        Deadline second = new Deadline("return book", LocalDate.of(2019, 6, 6));
        Event third = new Event(
                "book club meeting",
                new com.benthecat.kachow.parser.DateTimeParser.ParsedDateTime(
                        LocalDateTime.of(2019, 8, 6, 14, 0)),
                new com.benthecat.kachow.parser.DateTimeParser.ParsedDateTime(
                        LocalDateTime.of(2019, 8, 6, 16, 0)));
        Todo fourth = new Todo("wash car");
        TaskList tasks = new TaskList(List.of(first, second, third, fourth));

        List<TaskList.NumberedTask> matches = tasks.findByDescription("BOOK");

        assertEquals(List.of(1, 2, 3), matches.stream().map(TaskList.NumberedTask::number).toList());
        assertEquals(List.of(first, second, third), matches.stream().map(TaskList.NumberedTask::task).toList());
        assertTrue(tasks.findByDescription("race").isEmpty());
    }

    @Test
    void addAndReplace_duplicateDetails_preserveExistingTasks() throws KachowException {
        TaskList tasks = new TaskList();
        Todo first = new Todo("read book", true);
        tasks.add(first);
        tasks.add(new Todo("write book"));

        assertThrows(KachowException.class, () -> tasks.add(new Todo(" READ   BOOK ")));
        assertThrows(KachowException.class, () -> tasks.replace(2, new Todo("read book")));
        assertSame(first, tasks.get(1));
        assertEquals("write book", tasks.get(2).getDescription());
        tasks.replace(1, new Todo("read book", true));
        tasks.add(new Deadline("read book", LocalDate.of(2026, 9, 10)));
        tasks.add(new Deadline("read book", LocalDate.of(2026, 9, 11)));
        assertThrows(KachowException.class, () -> tasks.add(
                new Deadline("READ BOOK", LocalDate.of(2026, 9, 10), true)));
        assertEquals(4, tasks.getSize());
    }

    @Test
    void get_nonpositiveNumbers_throwsRecoverableError() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        assertThrows(KachowException.class, () -> tasks.get(0));
        assertThrows(KachowException.class, () -> tasks.get(-1));
    }

    /** Verifies that callers cannot mutate the task collection through its snapshot. */
    @Test
    void getTasks_returnedSnapshotCannotMutateTaskCollection() throws KachowException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        List<Task> snapshot = tasks.getTasks();

        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new Todo("write book")));
        assertEquals(1, tasks.getSize());
    }
    @Test
    void mutations_invalidNumbers_preserveListAndCompletionForEveryOperation() {
        Todo original = new Todo("read", true);
        TaskList tasks = new TaskList(List.of(original));
        for (int number : List.of(Integer.MIN_VALUE, -1, 0, 2, Integer.MAX_VALUE)) {
            assertThrows(KachowException.class, () -> tasks.get(number));
            assertThrows(KachowException.class, () -> tasks.mark(number));
            assertThrows(KachowException.class, () -> tasks.unmark(number));
            assertThrows(KachowException.class, () -> tasks.delete(number));
            assertThrows(KachowException.class, () -> tasks.replace(number, new Todo("replacement")));
            assertEquals(List.of(original), tasks.getTasks());
            assertTrue(original.isDone());
        }
    }

    @Test
    void constructorAndSnapshot_collectionCopies_preserveMembershipAcrossLaterMutations() throws KachowException {
        Todo original = new Todo("read");
        List<Task> source = new ArrayList<>(List.of(original));
        TaskList tasks = new TaskList(source);
        source.clear();
        List<Task> snapshot = tasks.getTasks();
        tasks.add(new Todo("write"));
        assertEquals(List.of(original), snapshot);
        assertEquals(2, tasks.getSize());
        tasks.delete(2);
        tasks.delete(1);
        assertTrue(tasks.isEmpty());
        assertEquals(List.of(original), snapshot);
        assertTrue(tasks.findByDescription("read").isEmpty());
        assertTrue(tasks.findOn(LocalDate.of(2026, 9, 11)).isEmpty());
        tasks.add(new Todo("new"));
        assertFalse(tasks.isEmpty());
        assertEquals("new", tasks.get(1).getDescription());
    }

    @Test
    void findByDescription_turkishDefaultLocale_usesStableCaseFolding() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            TaskList tasks = new TaskList(List.of(new Todo("WRITE report")));
            assertEquals(List.of(1), tasks.findByDescription("write").stream()
                    .map(TaskList.NumberedTask::number).toList());
        } finally {
            Locale.setDefault(original);
        }
    }

}
